package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudItemRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudRoomRepository;
import com.agonyforge.mud.models.dynamodb.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.agonyforge.mud.models.dynamodb.impl.Constants.TYPE_PC;

@Component
public class TellCommand extends AbstractCommand {
    @Autowired
    public TellCommand(MudCharacterRepository characterRepository,
                       MudItemRepository itemRepository,
                       MudRoomRepository roomRepository,
                       CommService commService) {
        super(characterRepository,
            itemRepository,
            roomRepository,
            commService);
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

        String message = Command.stripFirstWord(Command.stripFirstWord(input.getInput()));
        String targetName = tokens.get(1);
        MudCharacter ch = Command.getCharacter(characterRepository, webSocketContext, output);
        Optional<MudCharacter> targetOptional = characterRepository.getByType(TYPE_PC)
            .stream()
            .filter(c -> !c.isPrototype())
            .filter(c -> c.getName().toUpperCase(Locale.ROOT).startsWith(targetName))
            .findFirst();

        if (targetOptional.isEmpty()) {
            output.append("[default]There isn't anyone by that name.");
            return question;
        }

        MudCharacter target = targetOptional.get();

        if (ch.equals(target)) {
            output.append("[default]You mumble quietly to yourself.");
            return question;
        }

        output.append(String.format("[red]You tell %s, '%s[red]'", target.getName(), message));
        commService.sendTo(target, new Output(String.format("[red]%s tells you, '%s[red]'", ch.getName(), message)));

        return question;
    }
}
