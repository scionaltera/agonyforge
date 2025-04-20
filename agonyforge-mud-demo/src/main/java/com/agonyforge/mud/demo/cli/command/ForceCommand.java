package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component("forceCommand")
public class ForceCommand extends AbstractCommand {
    @Autowired
    public ForceCommand(
        RepositoryBundle repositoryBundle,
        CommService commService,
        ApplicationContext applicationContext
    ) {
        super(repositoryBundle, commService, applicationContext);
    }

    @Override
    public Question execute(
        Question question,
        WebSocketContext webSocketContext,
        List<String> tokens,
        Input input,
        Output output
    ) {
        // Validate arguments
        if (tokens.size() == 1) {
            output.append("[default]Who would you like to force?");
            return question;
        }
        if (tokens.size() == 2) {
            output.append("[default]What would you like to force them to do?");
            return question;
        }

        // Parse target name and forced command
        String targetName = tokens.get(1);
        String forcedCommand = input.getInput()
            .replaceFirst("(?i)force\\s+" + targetName + "\\s+", "")
            .trim();

        // Lookup executor and target characters
        MudCharacter executor = getCurrentCharacter(webSocketContext, output);
        Optional<MudCharacter> targetOpt = findRoomCharacter(executor, targetName);
        if (targetOpt.isEmpty()) {
            targetOpt = findWorldCharacter(executor, targetName);
        }
        if (targetOpt.isEmpty()) {
            output.append("[default]There isn't anyone by that name.");
            return question;
        }
        MudCharacter target = targetOpt.get();

        // Notify executor
        output.append(
            "[yellow]You force %s to: %s",
            target.getCharacter().getName(),
            forcedCommand
        );

        // Notify target
        getCommService().sendTo(
            target,
            new Output(
                "[red]%s FORCES you to: %s",
                executor.getCharacter().getName(),
                forcedCommand
            )
        );

        // Execute the forced command as the target
        getCommService().executeCommandAs(webSocketContext, target, forcedCommand);

        return question;
    }
}