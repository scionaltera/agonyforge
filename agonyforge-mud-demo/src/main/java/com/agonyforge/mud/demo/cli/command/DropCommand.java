package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.impl.MudItem;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudItemRepository;
import com.agonyforge.mud.models.dynamodb.service.CommService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Component
public class DropCommand implements Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(DropCommand.class);

    private final MudCharacterRepository characterRepository;
    private final MudItemRepository itemRepository;
    private final CommService commService;

    @Autowired
    public DropCommand(MudCharacterRepository characterRepository,
                       MudItemRepository itemRepository,
                       CommService commService) {
        this.characterRepository = characterRepository;
        this.itemRepository = itemRepository;
        this.commService = commService;
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
        Optional<MudCharacter> chOptional = Command.getCharacter(characterRepository, webSocketContext, output);

        if (chOptional.isEmpty()) {
            return question;
        }

        MudCharacter ch = chOptional.get();

        if (ch.getRoomId() == null) {
            output.append("[black]If you dropped anything here in the void, you'd probably never get it back.");
            return question;
        }

        if (tokens.size() == 1) {
            output.append("[default]What would you like to drop?");
            return question;
        }

        List<MudItem> items = itemRepository.getByCharacter(ch.getId());
        Optional<MudItem> targetOptional = items
            .stream()
            .filter(item -> item.getNameList()
                .stream()
                .anyMatch(name -> name.toUpperCase(Locale.ROOT).startsWith(tokens.get(1))))
            .findFirst();

        if (targetOptional.isEmpty()) {
            output.append("[default]You don't have anything like that.");
            return question;
        }

        MudItem target = targetOptional.get();
        target.setRoomId(ch.getRoomId());
        itemRepository.save(target);

        output.append(String.format("[default]You drop %s.", target.getShortDescription()));
        commService.sendToRoom(webSocketContext, ch.getRoomId(),
            new Output(String.format("[default]%s drops %s.", ch.getName(), target.getShortDescription())));

        return question;
    }
}
