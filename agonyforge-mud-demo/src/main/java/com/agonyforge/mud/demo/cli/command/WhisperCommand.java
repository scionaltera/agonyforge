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

@Component
public class WhisperCommand implements Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(WhisperCommand.class);
    private final MudCharacterRepository characterRepository;
    private final CommService commService;

    @Autowired
    public WhisperCommand(MudCharacterRepository characterRepository,
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
            output.append("[default]Who would you like to whisper to?");
            return question;
        }

        if (tokens.size() == 2) {
            output.append("[default]What would you like to whisper to them?");
            return question;
        }

        String message = Command.stripFirstWord(Command.stripFirstWord(input.getInput()));
        String targetName = tokens.get(1);
        Optional<MudCharacter> chOptional = Command.getCharacter(characterRepository, webSocketContext, output);

        if (chOptional.isEmpty()) {
            return question;
        }

        MudCharacter ch = chOptional.get();
        Optional<MudCharacter> targetOptional = characterRepository.getByRoom(ch.getRoomId())
            .stream()
            .filter(c -> c.getName().toUpperCase(Locale.ROOT).startsWith(targetName))
            .findFirst();

        if (targetOptional.isEmpty()) {
            output.append("[default]There isn't anyone by that name.");
            return question;
        }

        MudCharacter target = targetOptional.get();

        if (ch.equals(target)) {
            output.append("[default]You whisper quietly to yourself.");
            return question;
        }

        output.append(String.format("[red]You whisper to %s, '%s[red]'", target.getName(), message));
        commService.sendTo(target, new Output(String.format("[red]%s whispers to you, '%s[red]'", ch.getName(), message)));
        commService.sendToRoom(
            webSocketContext,
            ch.getRoomId(),
            new Output(String.format("[red]%s whispers something to %s.", ch.getName(), target.getName())),
            target);

        return question;
    }
}
