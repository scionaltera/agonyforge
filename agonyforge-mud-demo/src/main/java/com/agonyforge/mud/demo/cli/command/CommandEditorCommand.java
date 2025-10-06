package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.CommandReference;
import com.agonyforge.mud.demo.model.repository.CommandRepository;
import com.agonyforge.mud.demo.service.CommService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.agonyforge.mud.demo.cli.command.TokenType.*;

@Component
public class CommandEditorCommand extends AbstractCommand {
    static {
        addSyntax();
        addSyntax(WORD, WORD);
        addSyntax(WORD, WORD, NUMBER, WORD, QUOTED_WORDS);
    }

    private final CommandRepository commandRepository;

    @Autowired
    public CommandEditorCommand(RepositoryBundle repositoryBundle,
                                CommandRepository commandRepository,
                                CommService commService,
                                ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);
        this.commandRepository = commandRepository;
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
        if (tokens.size() <= 1) {
            output.append("[white]%-4s [white]%-12s [white]%-20s [white]%s", "Pri", "Command", "Bean Name", "Description");

            commandRepository.findAll().stream()
                .sorted(Comparator.comparingInt(CommandReference::getPriority))
                .forEachOrdered(cmd -> output.append("[green]%-4d [yellow]%-12s [dyellow]%-20s [dgreen]%s",
                    cmd.getPriority(),
                    cmd.getName().toUpperCase(Locale.ROOT),
                    cmd.getBeanName(),
                    cmd.getDescription()));

            return question;
        }

        String subCommand = tokens.get(1);

        if ("CREATE".equals(subCommand)) {
            // CEDIT CREATE <name> <priority> <beanName> <description>
            if (tokens.size() <= 5) {
                output.append("[yellow]CEDIT CREATE &lt;name&gt; &lt;priority&gt; &lt;beanName&gt; &lt;description&gt;");
            } else {
                try {
                    String[] rawTokens = StringUtils.tokenizeToStringArray(input.getInput(), " ", true, true);
                    String name = tokens.get(2);
                    int priority = Integer.parseInt(tokens.get(3));
                    String beanName = rawTokens[4];
                    String description = Command.stripFirstWords(input.getInput(), 5);
                    CommandReference command = new CommandReference();

                    // throws exception if bean cannot be found
                    getApplicationContext().getBean(beanName, Command.class);

                    command.setName(name);
                    command.setPriority(priority);
                    command.setBeanName(beanName);
                    command.setDescription(description);

                    commandRepository.save(command);

                    output.append("[green]Created %s command!", command.getName());
                } catch (NumberFormatException e) {
                    output.append("[red]Priority must be a number.");
                } catch (BeansException e) {
                    output.append("[red]No command bean could be found with that name.");
                }
            }
        } else if ("DELETE".equals(subCommand)) {
            // CEDIT DELETE <name>
            if (tokens.size() != 3) {
                output.append("[yellow]CEDIT DELETE &lt;name&gt;");
            } else {
                String name = Command.stripFirstWords(input.getInput(), 2);
                Optional<CommandReference> commandOptional = commandRepository.findByNameIgnoreCase(name);

                if (commandOptional.isPresent()) {
                    commandRepository.delete(commandOptional.get());
                    output.append("[yellow]Deleted command: %s", commandOptional.get().getName());
                } else {
                    output.append("[red]Unknown command: %s", name);
                }
            }
        } else {
            output
                .append("[yellow]Invalid subcommand.")
                .append("[yellow]Try with no arguments, CREATE or DELETE.");
        }

        return question;
    }
}
