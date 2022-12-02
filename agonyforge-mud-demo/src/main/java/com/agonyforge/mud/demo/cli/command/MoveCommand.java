package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.models.dynamodb.constant.Direction;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.impl.MudRoom;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudRoomRepository;
import com.agonyforge.mud.models.dynamodb.service.CommService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;

// intentionally not annotation with @Component
// the movement commands are configured in MoveConfiguration since they all share the same class
public class MoveCommand implements Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(MoveCommand.class);

    private final MudCharacterRepository characterRepository;
    private final MudRoomRepository roomRepository;
    private final CommService commService;
    private final Direction direction;

    public MoveCommand(MudCharacterRepository characterRepository,
                       MudRoomRepository roomRepository,
                       CommService commService,
                       Direction direction) {
        this.characterRepository = characterRepository;
        this.roomRepository = roomRepository;
        this.commService = commService;
        this.direction = direction;
    }

    @Override
    public Question execute(Question question,
                            WebSocketContext webSocketContext,
                            List<String> tokens,
                            Input input,
                            Output output) {

        Optional<MudCharacter> chOptional = getCharacter(webSocketContext, output);

        if (chOptional.isEmpty()) {
            return question;
        }

        MudCharacter ch = chOptional.get();
        Optional<MudRoom> roomOptional = roomRepository.getById(ch.getRoomId());

        if (roomOptional.isEmpty()) {
            output.append("[black]You are floating in the void, unable to move.");
            LOGGER.warn("{} is in the void!", ch.getName());

            return question;
        }

        MudRoom room = roomOptional.get();
        MudRoom.Exit exit = room.getExit(direction.getName());

        if (exit == null) {
            output.append("[default]Alas, you cannot go that way.");
            return question;
        }

        Optional<MudRoom> destOptional = roomRepository.getById(exit.getDestinationId());

        if (destOptional.isEmpty()) {
            output.append("[default]Alas, you cannot go that way.");
            LOGGER.error("Room {} exit {} leads to nonexistent room {}",
                room.getId(), direction.getName(), exit.getDestinationId());
            return question;
        }

        MudRoom destination = destOptional.get();

        commService.sendToRoom(webSocketContext, ch.getRoomId(),
            new Output(String.format("%s leaves %s.", ch.getName(), direction.getName())));

        ch.setRoomId(exit.getDestinationId());
        characterRepository.save(ch);

        commService.sendToRoom(webSocketContext, ch.getRoomId(),
            new Output(String.format("%s arrives from %s.", ch.getName(), direction.getOpposite())));

        output.append(LookCommand.doLook(characterRepository, ch, destination));

        return question;
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
