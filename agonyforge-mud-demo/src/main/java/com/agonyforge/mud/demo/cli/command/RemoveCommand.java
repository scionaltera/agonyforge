package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.models.dynamodb.constant.WearSlot;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.impl.MudItem;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudItemRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudRoomRepository;
import com.agonyforge.mud.models.dynamodb.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RemoveCommand extends AbstractCommand {
    @Autowired
    public RemoveCommand(MudCharacterRepository characterRepository,
                       MudItemRepository itemRepository,
                       MudRoomRepository roomRepository,
                       CommService commService) {
        super(characterRepository,
            itemRepository,
            roomRepository,
            commService);
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);

        if (tokens.size() == 1) {
            output.append("[default]What would you like to remove?");
            return question;
        }

        Optional<MudItem> targetOptional = findInventoryItem(ch, tokens.get(1));

        if (targetOptional.isEmpty()) {
            output.append("[default]You aren't wearing anything like that.");
            return question;
        }

        MudItem target = targetOptional.get();

        if (target.getWorn() == null) {
            output.append(String.format("[default]You aren't wearing %s[default].", target.getShortDescription()));
            return question;
        }

        WearSlot targetSlot = target.getWorn();
        target.setWorn(null);
        itemRepository.save(target);

        output.append(String.format("[default]You remove %s[default].", target.getShortDescription()));
        commService.sendToRoom(webSocketContext, ch.getRoomId(),
            new Output(String.format("[default]%s removes %s[default] from %s %s.",
                ch.getName(),
                target.getShortDescription(),
                ch.getPronoun().getPossessive(),
                targetSlot.getName()
            )));

        return question;
    }
}
