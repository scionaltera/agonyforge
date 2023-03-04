package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.impl.MudItem;
import com.agonyforge.mud.models.dynamodb.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class GetCommand extends AbstractCommand {
    @Autowired
    public GetCommand(RepositoryBundle repositoryBundle, CommService commService) {
        super(repositoryBundle, commService);
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);

        if (tokens.size() == 1) {
            output.append("[default]What would you like to get?");
            return question;
        }

        Optional<MudItem> targetOptional = findRoomItem(ch, tokens.get(1));

        if (targetOptional.isEmpty()) {
            output.append("[default]You don't see anything like that here.");
            return question;
        }

        MudItem target = targetOptional.get();
        target.setCharacterId(ch.getId());
        getRepositoryBundle().getItemRepository().save(target);

        output.append("[default]You get %s[default].", target.getShortDescription());
        getCommService().sendToRoom(webSocketContext, ch.getRoomId(),
            new Output("[default]%s gets %s[default].", ch.getName(), target.getShortDescription()));

        return question;
    }
}
