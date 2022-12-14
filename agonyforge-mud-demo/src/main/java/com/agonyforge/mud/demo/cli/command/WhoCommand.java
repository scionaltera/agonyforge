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

import static com.agonyforge.mud.models.dynamodb.impl.Constants.TYPE_PC;

@Component
public class WhoCommand extends AbstractCommand {
    @Autowired
    public WhoCommand(MudCharacterRepository characterRepository,
                      MudItemRepository itemRepository,
                      MudRoomRepository roomRepository,
                      CommService commService) {
        super(characterRepository,
            itemRepository,
            roomRepository,
            commService);
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
        List<MudCharacter> characters = characterRepository.getByType(TYPE_PC)
            .stream()
            .filter(ch -> !ch.isPrototype())
            .toList();

        output.append("[black]=== [white]Who is Playing [black]===", "");

        characters.forEach(ch -> output.append(String.format("[dwhite]%s",
            ch.getName())));

        output.append("", String.format("[white]%d player%s online.",
            characters.size(),
            characters.size() == 1 ? "" : "s"));

        return question;
    }
}
