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

import java.util.Map;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class EquipmentCommand extends AbstractCommand {
    @Autowired
    public EquipmentCommand(MudCharacterRepository characterRepository,
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
        Map<WearSlot, MudItem> inventory = itemRepository.getByCharacter(ch.getId())
                .stream()
                .filter(item -> item.getWorn() != null)
                .collect(Collectors.toMap(MudItem::getWorn, Function.identity()));

        output.append("[default]You are using:");

        if (inventory.isEmpty()) {
            output.append("Nothing.");
        } else {
            inventory.entrySet()
                .stream()
                .sorted()
                .forEach(entry -> output.append("[default]&lt;%s>\t%s",
                    entry.getKey().getPhrase(), entry.getValue().getShortDescription()));
        }

        return question;
    }
}
