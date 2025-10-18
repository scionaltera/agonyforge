package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.core.cli.Tokenizer;
import com.agonyforge.mud.demo.model.constant.AdminFlag;
import com.agonyforge.mud.demo.model.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Component
public class ObjectLookupService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectLookupService.class);

    private final RepositoryBundle repositoryBundle;

    public ObjectLookupService(RepositoryBundle repositoryBundle) {
        this.repositoryBundle = repositoryBundle;
    }

    public List<Binding> bind(MudCharacter ch, CommandReference command, List<String> tokens, List<TokenType> syntax) {
        if (tokens.size() != syntax.size()) {
            LOGGER.error("Number of tokens ({}) does not match syntax size ({})", tokens.size(), syntax.size());
            return new ArrayList<>();
        }

        List<Binding> bindings = new ArrayList<>();

        for (int i = 0; i < tokens.size(); i++) {
            switch (syntax.get(i)) {
                case COMMAND:
                    bindings.add(bindCommand(command, tokens.get(i), syntax.get(i)));
                    break;
                case WORD:
                    bindings.add(bindWord(tokens.get(i), syntax.get(i)));
                    break;
                case QUOTED_WORDS:
                    bindings.add(bindQuotedWords(tokens.get(i), syntax.get(i)));
                    break;
                case NUMBER:
                    bindings.add(bindNumber(tokens.get(i), syntax.get(i)));
                    break;
                case CHARACTER_IN_ROOM:
                    bindings.add(bindCharacterInRoom(ch, tokens.get(i), syntax.get(i)));
                    break;
                case CHARACTER_IN_WORLD:
                    bindings.add(bindCharacterInWorld(ch, tokens.get(i), syntax.get(i)));
                    break;
                case PLAYER_IN_ROOM:
                    bindings.add(bindPlayerInRoom(ch, tokens.get(i), syntax.get(i)));
                    break;
                case PLAYER_IN_WORLD:
                    bindings.add(bindPlayerInWorld(ch, tokens.get(i), syntax.get(i)));
                    break;
                case NPC_IN_ROOM:
                    bindings.add(bindNpcInRoom(ch, tokens.get(i), syntax.get(i)));
                    break;
                case NPC_IN_WORLD:
                    bindings.add(bindNpcInWorld(ch, tokens.get(i), syntax.get(i)));
                    break;
                case ITEM_HELD:
                    bindings.add(bindItemHeld(ch, tokens.get(i), syntax.get(i)));
                    break;
                case ITEM_WORN:
                    bindings.add(bindItemWorn(ch, tokens.get(i), syntax.get(i)));
                    break;
                case ITEM_GROUND:
                    bindings.add(bindItemGround(ch, tokens.get(i), syntax.get(i)));
                    break;
                case NPC_ID:
                    bindings.add(bindNpc(ch, tokens.get(i), syntax.get(i)));
                    break;
                case ROOM_ID:
                    bindings.add(bindRoom(ch, tokens.get(i), syntax.get(i)));
                    break;
                case ADMIN_FLAG:
                    bindings.add(bindAdminFlag(ch, tokens.get(i), syntax.get(i)));
                    break;
                default:
                    LOGGER.warn("No binding for token type {}", syntax.get(i));
                    return new ArrayList<>();
            }
        }

        return bindings;
    }

    Binding bindCommand(CommandReference command, String token, TokenType type) {
        return new Binding(type, token, command);
    }

    Binding bindWord(String token, TokenType type) {
        return new Binding(type, token, token);
    }

    Binding bindQuotedWords(String token, TokenType type) {
        return new Binding(type, token, token);
    }

    Binding bindNumber(String token, TokenType type) {
        try {
            return new Binding(type, token, Integer.parseInt(token));
        } catch (NumberFormatException e) {
            LOGGER.trace("Number format exception: {}", e.getMessage());
            throw new IllegalArgumentException("That's not a number.");
        }
    }

    Binding bindCharacterInRoom(MudCharacter ch, String token, TokenType type) {
        List<MudCharacter> targets = repositoryBundle.getCharacterRepository().findByLocationRoom(ch.getLocation().getRoom());
        Optional<MudCharacter> target = targets
            .stream()
            .filter(tch -> Tokenizer.tokenize(tch.getCharacter().getName()).stream()
                .filter(nameToken -> !nameToken.equalsIgnoreCase("A") && !nameToken.equalsIgnoreCase("AN") && !nameToken.equalsIgnoreCase("THE"))
                .anyMatch(nameToken -> nameToken.toUpperCase(Locale.ROOT).startsWith(token.toUpperCase(Locale.ROOT))))
            .findFirst();

        if (target.isPresent()) {
            if (target.get().equals(ch)) {
                throw new IllegalArgumentException("You don't see anybody else with that name.");
            }

            return new Binding(type, token, target.get());
        }

        LOGGER.trace("No character found in room with name {}", token);
        throw new IllegalArgumentException("You don't see anybody with that name.");
    }

    Binding bindPlayerInRoom(MudCharacter ch, String token, TokenType type) {
        List<MudCharacter> targets = repositoryBundle.getCharacterRepository().findByLocationRoom(ch.getLocation().getRoom());
        Optional<MudCharacter> target = targets
            .stream()
            .filter(tch -> Tokenizer.tokenize(tch.getCharacter().getName()).stream()
                .filter(nameToken -> !nameToken.equalsIgnoreCase("A") && !nameToken.equalsIgnoreCase("AN") && !nameToken.equalsIgnoreCase("THE"))
                .anyMatch(nameToken -> nameToken.toUpperCase(Locale.ROOT).startsWith(token.toUpperCase(Locale.ROOT))))
            .filter(tch -> tch.getPlayer() != null)
            .findFirst();

        if (target.isPresent()) {
            if (target.get().equals(ch)) {
                throw new IllegalArgumentException("You don't see anybody else with that name.");
            }

            return new Binding(type, token, target.get());
        }

        LOGGER.trace("No character found in room with name {}", token);
        throw new IllegalArgumentException("You don't see anybody with that name.");
    }

    Binding bindNpcInRoom(MudCharacter ch, String token, TokenType type) {
        List<MudCharacter> targets = repositoryBundle.getCharacterRepository().findByLocationRoom(ch.getLocation().getRoom());
        Optional<MudCharacter> target = targets
            .stream()
            .filter(tch -> Tokenizer.tokenize(tch.getCharacter().getName()).stream()
                .filter(nameToken -> !nameToken.equalsIgnoreCase("A") && !nameToken.equalsIgnoreCase("AN") && !nameToken.equalsIgnoreCase("THE"))
                .anyMatch(nameToken -> nameToken.toUpperCase(Locale.ROOT).startsWith(token.toUpperCase(Locale.ROOT))))
            .filter(tch -> tch.getPlayer() == null)
            .findFirst();

        if (target.isPresent()) {
            if (target.get().equals(ch)) {
                throw new IllegalArgumentException("You don't see anybody else with that name.");
            }

            return new Binding(type, token, target.get());
        }

        LOGGER.trace("No character found in room with name {}", token);
        throw new IllegalArgumentException("You don't see anybody with that name.");
    }

    Binding bindCharacterInWorld(MudCharacter ch, String token, TokenType type) {
        List<MudCharacter> targets = repositoryBundle.getCharacterRepository().findAll();
        Optional<MudCharacter> target = targets
            .stream()
            .filter(tch -> tch.getLocation() != null)
            .filter(tch -> Tokenizer.tokenize(tch.getCharacter().getName()).stream()
                .filter(nameToken -> !nameToken.equalsIgnoreCase("A") && !nameToken.equalsIgnoreCase("AN") && !nameToken.equalsIgnoreCase("THE"))
                .anyMatch(nameToken -> nameToken.toUpperCase(Locale.ROOT).startsWith(token.toUpperCase(Locale.ROOT))))
            .findFirst();

        if (target.isPresent()) {
            if (target.get().equals(ch)) {
                throw new IllegalArgumentException("You don't know of anybody else by that name.");
            }

            return new Binding(type, token, target.get());
        }

        LOGGER.trace("No character found in world with name: {}", token);
        throw new IllegalArgumentException("You don't know of anybody by that name.");
    }

    Binding bindPlayerInWorld(MudCharacter ch, String token, TokenType type) {
        List<MudCharacter> targets = repositoryBundle.getCharacterRepository().findAll();
        Optional<MudCharacter> target = targets
            .stream()
            .filter(tch -> tch.getLocation() != null)
            .filter(tch -> Tokenizer.tokenize(tch.getCharacter().getName()).stream()
                .filter(nameToken -> !nameToken.equalsIgnoreCase("A") && !nameToken.equalsIgnoreCase("AN") && !nameToken.equalsIgnoreCase("THE"))
                .anyMatch(nameToken -> nameToken.toUpperCase(Locale.ROOT).startsWith(token.toUpperCase(Locale.ROOT))))
            .filter(tch -> tch.getPlayer() != null)
            .findFirst();

        if (target.isPresent()) {
            if (target.get().equals(ch)) {
                throw new IllegalArgumentException("You don't know of anybody else by that name.");
            }

            return new Binding(type, token, target.get());
        }

        LOGGER.trace("No character found in world with name: {}", token);
        throw new IllegalArgumentException("You don't know of anybody by that name.");
    }

    Binding bindNpcInWorld(MudCharacter ch, String token, TokenType type) {
        List<MudCharacter> targets = repositoryBundle.getCharacterRepository().findAll();
        Optional<MudCharacter> target = targets
            .stream()
            .filter(tch -> tch.getLocation() != null)
            .filter(tch -> Tokenizer.tokenize(tch.getCharacter().getName()).stream()
                .filter(nameToken -> !nameToken.equalsIgnoreCase("A") && !nameToken.equalsIgnoreCase("AN") && !nameToken.equalsIgnoreCase("THE"))
                .anyMatch(nameToken -> nameToken.toUpperCase(Locale.ROOT).startsWith(token.toUpperCase(Locale.ROOT))))
            .filter(tch -> tch.getPlayer() == null)
            .findFirst();

        if (target.isPresent()) {
            if (target.get().equals(ch)) {
                throw new IllegalArgumentException("You don't know of anybody else by that name.");
            }

            return new Binding(type, token, target.get());
        }

        LOGGER.trace("No character found in world with name: {}", token);
        throw new IllegalArgumentException("You don't know of anybody by that name.");
    }

    Binding bindItemHeld(MudCharacter ch, String token, TokenType type) {
        List<MudItem> items = repositoryBundle.getItemRepository().findByLocationHeld(ch);

        Optional<MudItem> held = items
            .stream()
            .filter(item -> item.getLocation().getWorn() == null || item.getLocation().getWorn().isEmpty())
            .filter(item -> item.getItem().getNameList()
                .stream()
                .anyMatch(name -> name.toUpperCase(Locale.ROOT).startsWith(token.toUpperCase(Locale.ROOT))))
            .findFirst();

        if (held.isPresent()) {
            return new Binding(type, token, held.get());
        }

        LOGGER.trace("No item found held by character {} with name: {}", ch.getCharacter().getName(), token);
        throw new IllegalArgumentException("You aren't holding anything like that.");
    }

    Binding bindItemWorn(MudCharacter ch, String token, TokenType type) {
        List<MudItem> items = repositoryBundle.getItemRepository().findByLocationHeld(ch);

        Optional<MudItem> worn = items
            .stream()
            .filter(item -> item.getLocation().getWorn() != null && !item.getLocation().getWorn().isEmpty())
            .filter(item -> item.getItem().getNameList()
                .stream()
                .anyMatch(name -> name.toUpperCase(Locale.ROOT).startsWith(token.toUpperCase(Locale.ROOT))))
            .findFirst();

        if (worn.isPresent()) {
            return new Binding(type, token, worn.get());
        }

        LOGGER.trace("No item found worn by character {} with name: {}", ch.getCharacter().getName(), token);
        throw new IllegalArgumentException("You aren't wearing anything like that.");
    }

    Binding bindItemGround(MudCharacter ch, String token, TokenType type) {
        List<MudItem> items = repositoryBundle.getItemRepository().findByLocationRoom(ch.getLocation().getRoom());

        Optional<MudItem> ground = items
            .stream()
            .filter(item -> item.getItem().getNameList()
                .stream()
                .anyMatch(name -> name.toUpperCase(Locale.ROOT).startsWith(token.toUpperCase(Locale.ROOT))))
            .findFirst();

        if (ground.isPresent()) {
            return new Binding(type, token, ground.get());
        }

        LOGGER.trace("No item found in room with name: {}", token);
        throw new IllegalArgumentException("You don't see anything like that.");
    }

    Binding bindNpc(MudCharacter ch, String token, TokenType type) {
        Optional<MudCharacterTemplate> npc = repositoryBundle.getCharacterPrototypeRepository().findById(Long.parseLong(token));

        if (npc.isPresent()) {
            return new Binding(type, token, npc.get());
        }

        LOGGER.trace("No NPC found with ID {}", token);
        throw new IllegalArgumentException("There is no NPC with that number.");
    }

    Binding bindRoom(MudCharacter ch, String token, TokenType type) {
        Optional<MudRoom> room = repositoryBundle.getRoomRepository().findById(Long.parseLong(token));

        if (room.isPresent()) {
            return new Binding(type, token, room.get());
        }

        LOGGER.trace("No room found with ID {}", token);
        throw new IllegalArgumentException("There is no room with that number.");
    }

    Binding bindAdminFlag(MudCharacter ch, String token, TokenType type) {
        try {
            return new Binding(type, token, AdminFlag.valueOf(token));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("There is no admin flag with that name.");
        }
    }
}
