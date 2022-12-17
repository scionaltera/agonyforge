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

    static String stripFirstWord(String input) {
        int space = input.indexOf(' ');

        if (space == -1) {
            return "";
        }

        return input.substring(space + 1).stripLeading();
    }
}
