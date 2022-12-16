package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.impl.MudItem;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class InventoryCommand implements Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryCommand.class);

    private MudCharacterRepository characterRepository;
    private MudItemRepository itemRepository;

    @Autowired
    public InventoryCommand(MudCharacterRepository characterRepository,
                            MudItemRepository itemRepository) {
        this.characterRepository = characterRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
        Optional<MudCharacter> chOptional = Command.getCharacter(characterRepository, webSocketContext, output);

        if (chOptional.isEmpty()) {
            return question;
        }

        MudCharacter ch = chOptional.get();
        List<MudItem> items = itemRepository.getByCharacter(ch.getId());

        if (items.isEmpty()) {
            output.append("[default]You aren't carrying anything.");
        } else {
            output.append("[default]Your inventory:");
            items.forEach(item -> output.append(item.getShortDescription()));
        }

        return question;
    }
}
