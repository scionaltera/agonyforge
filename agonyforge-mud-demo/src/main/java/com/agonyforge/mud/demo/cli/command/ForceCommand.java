package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.service.CommService;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Component("forceCommand")
public class ForceCommand extends AbstractCommand {
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(ForceCommand.class);

    @Autowired
    public ForceCommand(RepositoryBundle repositoryBundle,
            CommService commService,
            ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);
    }

    @Override
    public Question execute(Question question,
            WebSocketContext webSocketContext,
            List<String> tokens,
            Input input,
            Output output) {

        if (tokens.size() == 1) {
            output.append("[default]Who would you like to force to?");
            return question;
        }

        if (tokens.size() == 2) {
            output.append("[default]What would you like to force them to do?");
            return question;
        }

        // Get the target player name and command to force
        String targetName = tokens.get(1);
        String forcedCommand = input.getInput().replaceFirst("(?i)force\\s+" + targetName + "\\s+", "").trim();

        // Get current character
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);

        // Find the target character (search in current room first, then globally)
        Optional<MudCharacter> targetOptional = findRoomCharacter(ch, targetName);

        if (targetOptional.isEmpty()) {
            targetOptional = findWorldCharacter(ch, targetName);
        }

        if (targetOptional.isEmpty()) {
            output.append("[default]There isn't anyone by that name.");
            return question;
        }

        MudCharacter target = targetOptional.get();

        // Send the notification
        output.append("[yellow]You force %s to: %s",
                target.getCharacter().getName(),
                forcedCommand);

        getCommService().sendTo(target,
                new Output("[red]%s FORCES you to: %s", ch.getPlayer().getUsername(), forcedCommand));

        // Execute the command using existing input pipeline
        getCommService().executeCommandAs(webSocketContext, target, forcedCommand);

        return question;
    }
}