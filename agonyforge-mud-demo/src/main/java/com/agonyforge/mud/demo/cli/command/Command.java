package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.CommandException;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;

public interface Command {
    Logger LOGGER = LoggerFactory.getLogger(Command.class);

    Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output);

    static MudCharacter getCharacter(MudCharacterRepository characterRepository,
                                               WebSocketContext webSocketContext,
                                               Output output) {

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

    static String stripFirstWord(String input) {
        int space = input.indexOf(' ');

        if (space == -1) {
            return "";
        }

        return input.substring(space + 1).stripLeading();
    }
}
