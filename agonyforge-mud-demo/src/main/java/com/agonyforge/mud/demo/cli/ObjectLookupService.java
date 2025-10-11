package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.core.cli.Tokenizer;
import com.agonyforge.mud.demo.cli.command.Command;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
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

    public List<Binding> bind(MudCharacter ch, Command command, List<String> tokens, List<TokenType> syntax) {
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
                default:
                    LOGGER.warn("No binding for token type {}", syntax.get(i));
                    return new ArrayList<>();
            }
        }

        return bindings;
    }

    Binding bindCommand(Command command, String token, TokenType type) {
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
}
