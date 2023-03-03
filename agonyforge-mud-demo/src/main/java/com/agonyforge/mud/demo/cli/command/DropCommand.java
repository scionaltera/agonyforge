package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.impl.MudItem;
import com.agonyforge.mud.models.dynamodb.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DropCommand extends AbstractCommand {
    @Autowired
    public DropCommand(RepositoryBundle repositoryBundle, CommService commService) {
        super(repositoryBundle, commService);
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);

        if (tokens.size() == 1) {
            output.append("[default]What would you like to drop?");
            return question;
        }

        Optional<MudItem> targetOptional = findInventoryItem(ch, tokens.get(1));

        if (targetOptional.isEmpty()) {
            output.append("[default]You don't have anything like that.");
            return question;
        }

        MudItem target = targetOptional.get();

        if (target.getWorn() != null) {
            output.append("[default]You need to remove it first.");
            return question;
        }

        target.setRoomId(ch.getRoomId());
        itemRepository.save(target);

        output.append("[default]You drop %s[default].", target.getShortDescription());
        commService.sendToRoom(webSocketContext, ch.getRoomId(),
            new Output("[default]%s drops %s[default].", ch.getName(), target.getShortDescription()));

        return question;
    }
}
