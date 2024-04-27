package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.constant.Direction;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudRoom;
import com.agonyforge.mud.demo.service.CommService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Optional;

// intentionally not annotation with @Component
// the movement commands are configured in MoveConfiguration since they all share the same class
public class MoveCommand extends AbstractCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(MoveCommand.class);

    private final Direction direction;

    public MoveCommand(RepositoryBundle repositoryBundle,
                       CommService commService,
                       ApplicationContext applicationContext,
                       Direction direction) {
        super(repositoryBundle, commService, applicationContext);

        this.direction = direction;
    }

    @Override
    public Question execute(Question question,
                            WebSocketContext webSocketContext,
                            List<String> tokens,
                            Input input,
                            Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);
        Optional<MudRoom> roomOptional = getRepositoryBundle().getRoomRepository().getById(ch.getRoomId());

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

        Optional<MudRoom> destOptional = getRepositoryBundle().getRoomRepository().getById(exit.getDestinationId());

        if (destOptional.isEmpty()) {
            output.append("[default]Alas, you cannot go that way.");
            LOGGER.error("Room {} exit {} leads to nonexistent room {}",
                room.getId(), direction.getName(), exit.getDestinationId());
            return question;
        }

        MudRoom destination = destOptional.get();

        getCommService().sendToRoom(webSocketContext, ch.getRoomId(),
            new Output("%s leaves %s.", ch.getName(), direction.getName()));

        ch.setRoomId(exit.getDestinationId());
        getRepositoryBundle().getCharacterRepository().save(ch);

        getCommService().sendToRoom(webSocketContext, ch.getRoomId(),
            new Output("%s arrives from %s.", ch.getName(), direction.getOpposite()));

        output.append(LookCommand.doLook(getRepositoryBundle(), ch, destination));

        return question;
    }
}
