package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.CommandReference;
import com.agonyforge.mud.demo.model.repository.CommandRepository;
import com.agonyforge.mud.demo.service.CommService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.agonyforge.mud.demo.cli.TokenType.*;

@Component
public class CommandEditorCommand extends AbstractCommand {
    private final CommandRepository commandRepository;

    @Autowired
    public CommandEditorCommand(RepositoryBundle repositoryBundle,
                                CommandRepository commandRepository,
                                CommService commService,
                                ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);
        this.commandRepository = commandRepository;

        addSyntax();                                       // list all commands
        addSyntax(WORD, COMMAND);                          // delete command
        addSyntax(WORD, WORD, NUMBER, WORD, QUOTED_WORDS); // create command
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<Binding> bindings, Output output) {
        if (bindings.size() == 1) {
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

        String subCommand = bindings.get(1).asString();

        if ("CREATE".equalsIgnoreCase(subCommand)) {
            String name = bindings.get(2).asString().toUpperCase(Locale.ROOT);
            int priority = bindings.get(3).asNumber().intValue();
            String beanName = bindings.get(4).asString();
            String description = bindings.get(5).asString();
            CommandReference command = new CommandReference();

            try {
                // throws exception if bean cannot be found
                getApplicationContext().getBean(beanName, Command.class);

                command.setName(name);
                command.setPriority(priority);
                command.setBeanName(beanName);
                command.setDescription(description);

                commandRepository.save(command);

                output.append("[green]Created %s command!", command.getName());
            } catch (BeansException e) {
                output.append("[red]No command bean could be found with that name.");
            }
        } else if ("DELETE".equalsIgnoreCase(subCommand)) {
            CommandReference command = bindings.get(2).asCommandReference();

            commandRepository.delete(command);
            output.append("[yellow]Deleted command: %s", command.getName());
        } else {
            output
                .append("[yellow]Invalid subcommand.")
                .append("[yellow]Try with no arguments, CREATE or DELETE.");
        }

        return question;
    }
}
