package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.service.SessionAttributeService;
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

// intentionally not annotated with @Component
// the movement commands are configured in MoveConfiguration since they all share the same class
public class MoveCommand extends AbstractCommand {
    static {
        addSyntax();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(MoveCommand.class);

    private final SessionAttributeService sessionAttributeService;
    private final Direction direction;

    public MoveCommand(RepositoryBundle repositoryBundle,
                       CommService commService,
                       SessionAttributeService sessionAttributeService,
                       ApplicationContext applicationContext,
                       Direction direction) {
        super(repositoryBundle, commService, applicationContext);

        this.sessionAttributeService = sessionAttributeService;
        this.direction = direction;
    }

    @Override
    public Question execute(Question question,
                            WebSocketContext webSocketContext,
                            List<String> tokens,
                            Input input,
                            Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);
        Optional<MudRoom> roomOptional = Optional.ofNullable(ch.getLocation().getRoom());

        if (roomOptional.isEmpty()) {
            output.append("[black]You are floating aimlessly in the void, unable to move.");
            LOGGER.warn("{} is in the void!", ch.getCharacter().getName());

            return question;
        }

        MudRoom room = roomOptional.get();
        MudRoom.Exit exit = room.getExit(direction.getName());

        if (exit == null) {
            output.append("[default]Alas, you cannot go that way.");
            return question;
        }

        Optional<MudRoom> destOptional = getRepositoryBundle().getRoomRepository().findById(exit.getDestinationId());

        if (destOptional.isEmpty()) {
            output.append("[default]Alas, you cannot go that way.");
            LOGGER.error("Room {} exit {} leads to nonexistent room {}",
                room.getId(), direction.getName(), exit.getDestinationId());
            return question;
        }

        MudRoom destination = destOptional.get();

        getCommService().sendToRoom(ch.getLocation().getRoom().getId(),
            new Output("%s leaves %s.", ch.getCharacter().getName(), direction.getName()), ch);

        ch.getLocation().setRoom(destination);
        getRepositoryBundle().getCharacterRepository().save(ch);

        getCommService().sendToRoom(ch.getLocation().getRoom().getId(),
            new Output("%s arrives from %s.", ch.getCharacter().getName(), direction.getOpposite()), ch);

        output.append(LookCommand.doLook(getRepositoryBundle(), sessionAttributeService, ch, destination));

        return question;
    }
}
