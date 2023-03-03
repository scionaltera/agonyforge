package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.command.Command;
import com.agonyforge.mud.models.dynamodb.impl.CommandReference;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.repository.CommandRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CommandQuestion extends AbstractQuestion {
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
                .append("[green]%s[default]> ", chOptional.get().getName());
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

        List<String> tokens = Arrays
            .stream(input.getInput().split(" "))
            .map(String::toUpperCase)
            .collect(Collectors.toList());
        List<CommandReference> refs = commandRepository.getByPriority();
        Optional<CommandReference> refOptional = refs
            .stream()
            .filter(ref -> ref.getName().startsWith(tokens.get(0)))
            .findFirst();

        if (refOptional.isEmpty()) {
            output.append("[default]Huh?");
            return new Response(this, output);
        }

        CommandReference ref = refOptional.get();

        try {
            Command command = applicationContext.getBean(ref.getBeanName(), Command.class);
            Question next = command.execute(this, webSocketContext, tokens, input, output);
            return new Response(next, output);
        } catch (CommandException e) {
            LOGGER.warn("Command failed: {}", e.getMessage());
            output.append("[red]Oops! Something went wrong...");
            return new Response(this, output);
        } catch (NoSuchBeanDefinitionException e) {
            LOGGER.warn("No Command found for beanName: {}", ref.getBeanName());
            output.append("[default]Huh?");
            return new Response(this, output);
        }
    }
}
