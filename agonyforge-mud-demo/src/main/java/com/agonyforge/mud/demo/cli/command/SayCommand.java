package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.models.dynamodb.repository.MudItemRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudRoomRepository;
import com.agonyforge.mud.models.dynamodb.service.CommService;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SayCommand extends AbstractCommand {

    @Autowired
    public SayCommand(MudCharacterRepository characterRepository,
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

        if (message.isBlank()) {
            output.append("[default]What would you like to say?");
            return question;
        }

        MudCharacter ch = Command.getCharacter(characterRepository, webSocketContext, output);

        output.append("[cyan]You say, '" + message + "[cyan]'");
        commService.sendToRoom(webSocketContext, ch.getRoomId(), new Output(String.format("[cyan]%s says, '%s[cyan]'", ch.getName(), message)));

        return question;
    }
}
