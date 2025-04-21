package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Tokenizer;
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

        // Check that the forced command exists as a bean
        String[] parts = forcedCommand.split("\\s+");
        String cmdName = parts[0].toLowerCase();
        String beanName = cmdName + "Command";
        if (!applicationContext.containsBean(beanName)) {
            output.append("[default]That command does not exist: %s", cmdName);
            return question;
        }

        // Validate forced command arguments by dry-running the command
        List<String> forcedTokens = Tokenizer.tokenize(forcedCommand);
        @SuppressWarnings("unchecked")
        com.agonyforge.mud.demo.cli.command.Command cmdBean = (com.agonyforge.mud.demo.cli.command.Command) applicationContext
                .getBean(beanName);

        Output validationOutput = new Output();
        Question cmdQuestion = applicationContext.getBean("commandQuestion", Question.class);
        cmdBean.execute(cmdQuestion, webSocketContext, forcedTokens, new Input(forcedCommand), validationOutput);

        String validationText = validationOutput.toString();
        if (validationText.startsWith("[default]")) {
            output.append(validationText);
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
                "[yellow]You force %s to: %s",
                target.getCharacter().getName(),
                forcedCommand);

        // Notify target
        getCommService().sendTo(
                target,
                new Output(
                        "[red]%s FORCES you to: %s",
                        executor.getCharacter().getName(),
                        forcedCommand));

        // Execute the forced command as the target
        getCommService().executeCommandAs(webSocketContext, target, forcedCommand);

        return question;
    }
}