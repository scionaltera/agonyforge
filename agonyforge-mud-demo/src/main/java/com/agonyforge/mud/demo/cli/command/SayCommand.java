package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.service.EchoService;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;

@Component
public class SayCommand implements Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(SayCommand.class);
    private final MudCharacterRepository characterRepository;
    private final EchoService echoService;

    @Autowired
    public SayCommand(MudCharacterRepository characterRepository,
                      EchoService echoService) {
        this.characterRepository = characterRepository;
        this.echoService = echoService;
    }

    @Override
    public Question execute(Question question,
                            WebSocketContext webSocketContext,
                            Input input,
                            Output output) {
        String message = stripFirstWord(input.getInput());

        if (message.isBlank()) {
            output.append("[default]What would you like to say?");
            return question;
        }

        Optional<MudCharacter> chOptional = getCharacter(webSocketContext, output);

        if (chOptional.isPresent()) {
            MudCharacter ch = chOptional.get();

            output.append("[cyan]You say, '" + message + "[cyan]'");
            echoService.echoToAll(webSocketContext, new Output(String.format("[cyan]%s says, '%s[cyan]'", ch.getName(), message)));
        }

        return question;
    }

    private String stripFirstWord(String input) {
        int space = input.indexOf(' ');

        if (space == -1) {
            return "";
        }

        return input.substring(space + 1).stripLeading();
    }

    private Optional<MudCharacter> getCharacter(WebSocketContext webSocketContext, Output output) {
        UUID chId = (UUID) webSocketContext.getAttributes().get(MUD_CHARACTER);
        Optional<MudCharacter> chOptional = characterRepository.getById(chId, true);

        if (chOptional.isEmpty()) {
            LOGGER.error("Cannot look up character by ID: {}", chId);
            output.append("[red]Unable to find your character! The error has been reported.");
            return Optional.empty();
        }

        return chOptional;
    }
}
