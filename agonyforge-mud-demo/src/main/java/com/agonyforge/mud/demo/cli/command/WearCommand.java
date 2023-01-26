package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
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
public class WearCommand extends AbstractCommand {
    @Autowired
    public WearCommand(MudCharacterRepository characterRepository,
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
            output.append("[default]What would you like to wear?");
            return question;
        }

        Optional<MudItem> targetOptional = findInventoryItem(ch, tokens.get(1));

        if (targetOptional.isEmpty()) {
            output.append("[default]You aren't carrying anything like that.");
            return question;
        }

        MudItem target = targetOptional.get();

        if (target.getWorn() != null) {
            output.append(String.format("[default]You are already wearing %s[default].", target.getShortDescription()));
            return question;
        }

        // find all items already worn
        List<MudItem> wornItems = itemRepository.getByCharacter(ch.getId())
            .stream()
            .filter(item -> item.getWorn() != null)
            .toList();

        // find all slots that the character has AND the target item has AND isn't already occupied
        List<String> candidateSlots = target.getWearSlots()
            .stream()
            .filter(slot -> ch.getWearSlots().contains(slot))
            .filter(slot -> wornItems.stream().noneMatch(item -> item.getWorn().equals(slot)))
            .toList();

        if (candidateSlots.isEmpty()) {
            output.append("[default]You need to take something else off before you can wear that.");
            return question;
        }

        String targetSlot = candidateSlots.get(0);
        target.setWorn(targetSlot);
        itemRepository.save(target);

        output.append(String.format("[default]You wear %s[default] on your %s.", target.getShortDescription(), targetSlot));
        commService.sendToRoom(webSocketContext, ch.getRoomId(),
            new Output(String.format("[default]%s wears %s[default] on %s %s.",
                ch.getName(),
                target.getShortDescription(),
                "their", // TODO pronouns
                targetSlot
            )));

        return question;
    }
}