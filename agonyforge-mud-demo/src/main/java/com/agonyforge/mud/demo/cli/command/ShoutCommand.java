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
import java.util.Optional;

@Component
public class ShoutCommand extends AbstractCommand {
    @Autowired
    public ShoutCommand(MudCharacterRepository characterRepository,
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
        String message = Command.stripFirstWord(input.getInput());
        MudCharacter ch = Command.getCharacter(characterRepository, webSocketContext, output);

        if (message.isBlank()) {
            output.append("[default]What would you like to shout?");
            return question;
        }

        output.append("[dyellow]You shout, '" + message + "[dyellow]'");
        commService.sendToZone(webSocketContext, ch.getZoneId(), new Output(String.format("[dyellow]%s shouts, '%s[dyellow]'", ch.getName(), message)));

        return question;
    }
}
