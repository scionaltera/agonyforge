package com.agonyforge.mud.demo.cli.question.ingame;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.cli.Tokenizer;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.cli.command.Command;
import com.agonyforge.mud.demo.cli.question.BaseQuestion;
import com.agonyforge.mud.demo.cli.question.CommandException;
import com.agonyforge.mud.demo.model.impl.CommandReference;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.repository.CommandRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.*;

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
                .append("[green]%s[default]> ", chOptional.get().getCharacter().getName());
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

        List<String> tokens = Tokenizer.tokenize(input.getInput());
        Optional<CommandReference> refOptional = commandRepository.findFirstByNameStartingWith(tokens.get(0), Sort.by(Sort.Order.asc("priority")));

        if (refOptional.isEmpty()) {
            output.append("[default]Huh?");
            return new Response(this, output);
        }

        CommandReference ref = refOptional.get();

        try {
            Command command = applicationContext.getBean(ref.getBeanName(), Command.class);
            MudCharacter ch = getCharacter(webSocketContext, output).orElseThrow();

            if (1L == ch.getTemplate().getId() || (ch.getPlayer() != null && ch.getPlayer().getRoles().stream().anyMatch(role -> role.getCommands().contains(ref)))) {
                Question next = command.execute(this, webSocketContext, tokens, input, output);
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
