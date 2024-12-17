package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudRoom;
import com.agonyforge.mud.demo.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.agonyforge.mud.demo.cli.question.ingame.olc.room.RoomEditorQuestion.REDIT_MODEL;

@Component
public class RoomEditorCommand extends AbstractCommand {
    private final ApplicationContext applicationContext;

    @Autowired
    public RoomEditorCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);

        this.applicationContext = applicationContext;
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);
        MudRoom room;

        // REDIT           <-- edit the room you're standing in
        // REDIT <number>  <-- edit an existing room by number
        // REDIT <number>  <-- create a new room with that number

        if (tokens.size() > 1) {
            try {
                Long roomId = Long.parseLong(tokens.get(1));
                Optional<MudRoom> roomOptional = getRepositoryBundle().getRoomRepository().findById(roomId);

                if (roomOptional.isEmpty()) {
                    String roomIdString = tokens.get(1);
                    Long zoneId = Long.parseLong(tokens.get(1).substring(0, roomIdString.length() - 2));

                    LOGGER.info("Creating room {} in zone {}", roomId, zoneId);

                    room = new MudRoom();

                    room.setId(roomId);
                    room.setZoneId(zoneId);
                    room.setName("Unfinished Room");
                    room.setDescription("A newly constructed room. It still needs a coat of paint.");

                    getRepositoryBundle().getRoomRepository().save(room);
                } else {
                    room = roomOptional.get();
                }

                webSocketContext.getAttributes().put(REDIT_MODEL, room);
            } catch (NumberFormatException e) {
                LOGGER.error(e.getMessage());
                output
                    .append("Unrecognized token: '%s'", tokens)
                    .append("[default]Valid arguments:")
                    .append("REDIT")
                    .append("REDIT &lt;room number&gt;");
                return question;
            }
        } else {
            room = getRepositoryBundle().getRoomRepository().findById(ch.getRoomId()).orElseThrow();
            webSocketContext.getAttributes().put(REDIT_MODEL, room);
        }

        getCommService().sendToRoom(webSocketContext, ch.getRoomId(),
            new Output("[yellow]%s begins editing.", ch.getName()), ch);

        return applicationContext.getBean("roomEditorQuestion", Question.class);
    }
}
