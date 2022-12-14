package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.impl.MudRoom;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudItemRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudRoomRepository;
import com.agonyforge.mud.models.dynamodb.service.CommService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Component
public class LookCommand extends AbstractCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(LookCommand.class);

    public static Output doLook(MudCharacterRepository characterRepository,
                                MudItemRepository itemRepository,
                                MudCharacter ch,
                                MudRoom room) {

        Output output = new Output();

        output
            .append(String.format("[yellow](%d) %s", room.getId(), room.getName()))
            .append(String.format("[dwhite]%s", room.getDescription()))
            .append(String.format("[dcyan]Exits: %s", String.join(", ", room.getExits())));

        characterRepository.getByRoom(room.getId())
            .stream()
            .filter(target -> !target.equals(ch))
            .forEach(target -> output.append(String.format("[green]%s is here.", target.getName())));

        itemRepository.getByRoom(room.getId())
            .forEach(target -> output.append(String.format("[green]%s",
                StringUtils.capitalize(target.getLongDescription()))));

        return output;
    }

    @Autowired
    public LookCommand(MudCharacterRepository characterRepository,
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
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);
        Optional<MudRoom> roomOptional = roomRepository.getById(ch.getRoomId());

        if (roomOptional.isEmpty()) {
            output.append("[black]You are floating in the void...");
            LOGGER.error("{} is floating in the void!", ch.getName());

            return question;
        }

        MudRoom room = roomOptional.get();

        output.append(doLook(characterRepository, itemRepository, ch, room));

        return question;
    }
}
