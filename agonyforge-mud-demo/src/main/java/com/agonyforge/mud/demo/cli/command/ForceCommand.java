package com.agonyforge.mud.demo.cli.command;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.service.CommService;

@Component("forceCommand")
public class ForceCommand extends AbstractCommand {
    private static final Logger LOG = LoggerFactory.getLogger(ForceCommand.class);
    private final ApplicationContext applicationContext;

    @Autowired
    public ForceCommand(
            RepositoryBundle repositoryBundle,
            CommService commService,
            ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);
        this.applicationContext = applicationContext;
    }

    @Override
    public Question execute(
            Question question,
            WebSocketContext webSocketContext,
            List<String> tokens,
            Input input,
            Output output) {
        // Validate argument count
        if (tokens.size() == 1) {
            output.append("[default]Who would you like to force?");
            return question;
        }
        if (tokens.size() == 2) {
            output.append("[default]What would you like to force them to do?");
            return question;
        }

        // Parse target name and forced command text
        String targetName = tokens.get(1);
        String forcedCommand = input.getInput()
                .replaceFirst("(?i)force\\s+" + targetName + "\\s+", "")
                .trim();

        // Check for nested force command
        if (forcedCommand.toLowerCase().startsWith("force")) {
            output.append("[default]You cannot force someone to force others!");
            return question;
        }

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
                "[yellow]You forced %s to '%s[yellow]'!",
                target.getCharacter().getName(),
                forcedCommand);

        // Notify target
        getCommService().sendTo(
                target,
                new Output(
                        "[red]%s FORCES you to '%s[red]'.",
                        executor.getCharacter().getName(),
                        forcedCommand));

        // Log the forced command usage
        LOG.info("{} forced {} to run: {}", executor.getName(), target.getName(), forcedCommand);

        // Execute the forced command as the target
        getCommService().executeCommandAs(webSocketContext, target, forcedCommand);

        return question;
    }

    private MudCharacter getCharacter(WebSocketContext webSocketContext) {
        return (MudCharacter) webSocketContext.getAttributes().get("character");
    }
}