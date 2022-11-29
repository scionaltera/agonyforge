package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import com.agonyforge.mud.models.dynamodb.service.CommService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.TYPE_PC;

@Component
public class TellCommand implements Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(TellCommand.class);
    private final MudCharacterRepository characterRepository;
    private final CommService commService;

    @Autowired
    public TellCommand(MudCharacterRepository characterRepository,
                       CommService commService) {
        this.characterRepository = characterRepository;
        this.commService = commService;
    }

    @Override
    public Question execute(Question question,
                            WebSocketContext webSocketContext,
                            List<String> tokens,
                            Input input,
                            Output output) {

        if (tokens.size() == 1) {
            output.append("[default]Who would you like to tell?");
            return question;
        }

        if (tokens.size() == 2) {
            output.append("[default]What would you like to tell them?");
            return question;
        }

        String message = stripFirstWord(stripFirstWord(input.getInput()));
        String targetName = tokens.get(1);
        Optional<MudCharacter> chOptional = getCharacter(webSocketContext, output);
        Optional<MudCharacter> targetOptional = characterRepository.getByType(TYPE_PC)
            .stream()
            .filter(c -> !c.isPrototype())
            .filter(c -> c.getName().toUpperCase(Locale.ROOT).startsWith(targetName))
            .findFirst();

        if (chOptional.isEmpty()) {
            return question;
        }

        if (targetOptional.isEmpty()) {
            output.append("[default]There isn't anyone by that name.");
            return question;
        }

        MudCharacter ch = chOptional.get();
        MudCharacter target = targetOptional.get();

        if (ch.equals(target)) {
            output.append("[default]You mumble quietly to yourself.");
            return question;
        }

        output.append(String.format("[red]You tell %s, '%s[red]'", target.getName(), message));
        commService.sendTo(target, new Output(String.format("[red]%s tells you, '%s[red]'", ch.getName(), message)));

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
        Optional<MudCharacter> chOptional = characterRepository.getById(chId, false);

        if (chOptional.isEmpty()) {
            LOGGER.error("Cannot look up character by ID: {}", chId);
            output.append("[red]Unable to find your character! The error has been reported.");
            return Optional.empty();
        }

        return chOptional;
    }
}
