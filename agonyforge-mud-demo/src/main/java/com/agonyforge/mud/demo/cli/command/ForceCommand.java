package com.agonyforge.mud.demo.cli.command;

import java.util.List;

import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.cli.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.service.CommService;

@Component("forceCommand")
public class ForceCommand extends AbstractCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(ForceCommand.class);

    @Autowired
    public ForceCommand(
            RepositoryBundle repositoryBundle,
            CommService commService,
            ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);

        addSyntax(TokenType.CHARACTER_IN_WORLD, TokenType.COMMAND, TokenType.QUOTED_WORDS);
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<Binding> bindings, Output output) {
        MudCharacter target = bindings.get(1).asCharacter();
        Command command = bindings.get(2).asCommand();
        String args = bindings.get(3).asString();

        if (command instanceof ForceCommand) {
            output.append("[default]You cannot force someone to force others!");
            return question;
        }

        // Notify executor
        output.append(
            "[yellow]You FORCE %s to '%s %s[yellow]'!",
            target.getCharacter().getName(),
            command, args);

        // Notify target
        getCommService().sendTo(
            target,
            new Output(
                "[red]%s FORCES you to '%s %s[red]'.",
                target.getCharacter().getName(),
                command, args));

        // Log the forced command usage
        LOGGER.info("{} forced {} to run: {}", target.getName(), target.getName(), command);

        // Execute the forced command as the target
        getCommService().executeCommandAs(webSocketContext, target, command + " " + args);

        return question;
    }
}
