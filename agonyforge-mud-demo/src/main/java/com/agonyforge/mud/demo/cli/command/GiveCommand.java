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
public class GiveCommand implements Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(GiveCommand.class);

    private final MudCharacterRepository characterRepository;
    private final MudItemRepository itemRepository;
    private final CommService commService;

    @Autowired
    public GiveCommand(MudCharacterRepository characterRepository,
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
            output.append("[black]There is nobody else here with you in the endless void.");
            return question;
        }

        if (tokens.size() == 1) {
            output.append("[default]Which item do you want to give?");
            return question;
        }

        if (tokens.size() == 2) {
            output.append("[default]Who do you want to give it to?");
            return question;
        }

        List<MudItem> items = itemRepository.getByCharacter(ch.getId());
        Optional<MudItem> itemOptional = items
            .stream()
            .filter(item -> item.getNameList()
                .stream()
                .anyMatch(name -> name.toUpperCase(Locale.ROOT).startsWith(tokens.get(1))))
            .findFirst();

        if (itemOptional.isEmpty()) {
            output.append("[default]You don't have anything like that.");
            return question;
        }

        List<MudCharacter> targets = characterRepository.getByRoom(ch.getRoomId());
        Optional<MudCharacter> targetOptional = targets
            .stream()
            .filter(tch -> tch.getName().toUpperCase(Locale.ROOT).startsWith(tokens.get(2)))
            .findFirst();

        if (targetOptional.isEmpty()) {
            output.append("[default]You don't see anyone by that name here.");
            return question;
        }

        MudItem item = itemOptional.get();
        MudCharacter target = targetOptional.get();

        if (ch.equals(target)) {
            output.append("[default]You offer it to yourself, and graciously accept.");
            return question;
        }

        item.setCharacterId(target.getId());
        itemRepository.save(item);

        output.append(String.format("[default]You give %s to %s.", item.getShortDescription(), target.getName()));
        commService.sendTo(target, new Output(String.format("[default]%s gives %s to you.", ch.getName(), item.getShortDescription())));
        commService.sendToRoom(webSocketContext,
            ch.getRoomId(),
            new Output(String.format("[default]%s gives %s to %s.", ch.getName(), item.getShortDescription(), target.getName())),
            target);

        return question;
    }
}
