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
public class GossipCommand extends AbstractCommand {
    @Autowired
    public GossipCommand(RepositoryBundle repositoryBundle, CommService commService) {
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
            output.append("[default]What would you like to gossip?");
            return question;
        }

        MudCharacter ch = getCurrentCharacter(webSocketContext, output);

        output.append("[green]You gossip, '%s[green]'", message);
        getCommService().sendToAll(webSocketContext, new Output("[green]%s gossips, '%s[green]'", ch.getName(), message));

        return question;
    }
}
