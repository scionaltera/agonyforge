package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.CommandException;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.impl.MudItem;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudItemRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudRoomRepository;
import com.agonyforge.mud.models.dynamodb.service.CommService;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.TYPE_PC;

public abstract class AbstractCommand implements Command {
    protected final MudCharacterRepository characterRepository;
    protected final MudItemRepository itemRepository;
    protected final MudRoomRepository roomRepository;
    protected final CommService commService;

    public AbstractCommand(RepositoryBundle repositoryBundle,
                           CommService commService) {
        this.characterRepository = repositoryBundle.getCharacterRepository();
        this.itemRepository = repositoryBundle.getItemRepository();
        this.roomRepository = repositoryBundle.getRoomRepository();
        this.commService = commService;
    }

    protected MudCharacter getCurrentCharacter(WebSocketContext webSocketContext, Output output) {
        UUID chId = (UUID) webSocketContext.getAttributes().get(MUD_CHARACTER);
        Optional<MudCharacter> chOptional = characterRepository.getById(chId, false);

        if (chOptional.isEmpty()) {
            LOGGER.error("Cannot look up character by ID: {}", chId);
            output.append("[red]Unable to find your character! The error has been reported.");
            throw new CommandException("Unable to load character: " + chId);
        }

        MudCharacter ch = chOptional.get();

        if (ch.getRoomId() == null) {
            output.append("[black]You are floating aimlessly in the void.");
            throw new CommandException("Character is in the void: " + chId);
        }

        return ch;
    }

    protected Optional<MudItem> findInventoryItem(MudCharacter ch, String token) {
        List<MudItem> items = itemRepository.getByCharacter(ch.getId());

        return items
            .stream()
            .filter(item -> item.getNameList()
                .stream()
                .anyMatch(name -> name.toUpperCase(Locale.ROOT).startsWith(token.toUpperCase(Locale.ROOT))))
            .findFirst();
    }

    protected Optional<MudItem> findRoomItem(MudCharacter ch, String token) {
        List<MudItem> items = itemRepository.getByRoom(ch.getRoomId());

        return items
            .stream()
            .filter(item -> item.getNameList()
                .stream()
                .anyMatch(name -> name.toUpperCase(Locale.ROOT).startsWith(token.toUpperCase(Locale.ROOT))))
            .findFirst();
    }

    protected Optional<MudCharacter> findRoomCharacter(MudCharacter ch, String token) {
        List<MudCharacter> targets = characterRepository.getByRoom(ch.getRoomId());

        return targets
            .stream()
            .filter(tch -> !tch.equals(ch))
            .filter(tch -> tch.getName().toUpperCase(Locale.ROOT).startsWith(token.toUpperCase(Locale.ROOT)))
            .findFirst();
    }

    protected Optional<MudCharacter> findWorldCharacter(MudCharacter ch, String token) {
        List<MudCharacter> targets = characterRepository.getByType(TYPE_PC);

        return targets
            .stream()
            .filter(tch -> !tch.equals(ch))
            .filter(tch -> !tch.isPrototype())
            .filter(tch -> tch.getName().toUpperCase(Locale.ROOT).startsWith(token.toUpperCase(Locale.ROOT)))
            .findFirst();
    }
}
