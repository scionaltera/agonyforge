package com.agonyforge.mud.demo.cli.question.ingame;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.cli.Tokenizer;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.cli.command.AbstractCommand;
import com.agonyforge.mud.demo.cli.command.SyntaxAwareTokenizer;
import com.agonyforge.mud.demo.cli.command.TokenType;
import com.agonyforge.mud.demo.cli.question.BaseQuestion;
import com.agonyforge.mud.demo.cli.question.CommandException;
import com.agonyforge.mud.demo.model.impl.CommandReference;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.Role;
import com.agonyforge.mud.demo.model.repository.CommandRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class CommandQuestion extends BaseQuestion {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandQuestion.class);

    private final ApplicationContext applicationContext;
    private final CommandRepository commandRepository;

    @Autowired
    public CommandQuestion(ApplicationContext applicationContext,
                           RepositoryBundle repositoryBundle,
                           CommandRepository commandRepository) {
        super(applicationContext, repositoryBundle);
        this.applicationContext = applicationContext;
        this.commandRepository = commandRepository;
    }

    @Override
    public Output prompt(WebSocketContext wsContext) {
        Output output = new Output();
        Optional<MudCharacter> chOptional = getCharacter(wsContext, output);

        if (chOptional.isPresent()) {
            output
                .append("")
                .append("[green]%s [red]%d[dred]/[red]%d[default]> ",
                    chOptional.get().getCharacter().getName(),
                    chOptional.get().getCharacter().getHitPoints(),
                    chOptional.get().getCharacter().getMaxHitPoints());
        } else {
            output
                .append("")
                .append("[default]> ");
        }

        return output;
    }

    @Override
    public Response answer(WebSocketContext webSocketContext, Input input) {
        Output output = new Output();

        if (input.getInput().isBlank()) {
            return new Response(this, output);
        }

        String cmdName = Tokenizer.tokenize(input.getInput()).get(0);
        Optional<CommandReference> refOptional = commandRepository.findFirstByNameStartingWith(cmdName.toUpperCase(Locale.ROOT), Sort.by(Sort.Order.asc("priority")));

        if (refOptional.isEmpty()) {
            output.append("[default]Huh?");
            return new Response(this, output);
        }

        CommandReference ref = refOptional.get();

        try {
            AbstractCommand command = applicationContext.getBean(ref.getBeanName(), AbstractCommand.class);
            MudCharacter ch = getCharacter(webSocketContext, output).orElseThrow();

            if (ch.getPlayer() != null && (ch.getPlayer().getRoles().stream().anyMatch(role -> role.getCommands().contains(ref)) || ch.getPlayer().getRoles().stream().anyMatch(Role::isImplementor))) {
                List<String> tokens = null;

                for (List<TokenType> syntax : command.getSyntaxes()) {
                    try {
                        tokens = SyntaxAwareTokenizer.tokenize(input.getInput(), syntax);
                        break;
                    } catch (IllegalArgumentException e) {
                        LOGGER.trace("Illegal syntax: {}", e.getMessage());
                    }
                }

                if (tokens == null) {
                    output
                        .append("[yellow]:: [white]%s [yellow]::", ref.getName().toUpperCase(Locale.ROOT))
                        .append("[dyellow]Description: %s", ref.getDescription())
                        .append("[yellow]Usage:");
                    command.getSyntaxes().forEach(syntax -> output.append("  [yellow]%s %s",
                        ref.getName().toUpperCase(Locale.ROOT),
                        syntax.stream().map(TokenType::toString).collect(Collectors.joining(" "))));
                    return new Response(this, output);
                }

                Question next = command.execute(this, webSocketContext, tokens, output);
                return new Response(next, output);
            }

            LOGGER.warn("Request by {} ({}) to use command {} denied due to missing role", ch.getPlayer().getUsername(), ch.getCharacter().getName(), ref.getName());
        } catch (CommandException e) {
            LOGGER.warn("Command failed: {}", e.getMessage());
            output.append("[red]Oops! Something went wrong... the error has been reported!");
            return new Response(this, output);
        } catch (NoSuchBeanDefinitionException e) {
            LOGGER.warn("No Command found for beanName: {}", ref.getBeanName());
        }

        output.append("[default]Huh?");
        return new Response(this, output);
    }
}
