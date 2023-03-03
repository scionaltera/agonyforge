package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShoutCommand extends AbstractCommand {
    @Autowired
    public ShoutCommand(RepositoryBundle repositoryBundle, CommService commService) {
        super(repositoryBundle, commService);
    }

    @Override
    public Question execute(Question question,
                            WebSocketContext webSocketContext,
                            List<String> tokens,
                            Input input,
                            Output output) {
        String message = Command.stripFirstWord(input.getInput());

        if (message.isBlank()) {
            output.append("[default]What would you like to shout?");
            return question;
        }

        MudCharacter ch = getCurrentCharacter(webSocketContext, output);

        output.append("[dyellow]You shout, '%s[dyellow]'", message);
        commService.sendToZone(webSocketContext, ch.getZoneId(), new Output("[dyellow]%s shouts, '%s[dyellow]'", ch.getName(), message));

        return question;
    }
}
