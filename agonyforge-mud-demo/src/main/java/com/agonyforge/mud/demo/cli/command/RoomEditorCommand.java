package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudRoom;
import com.agonyforge.mud.demo.service.CommService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.agonyforge.mud.demo.cli.TokenType.*;
import static com.agonyforge.mud.demo.cli.question.ingame.olc.room.RoomEditorQuestion.REDIT_MODEL;

@Component
public class RoomEditorCommand extends AbstractCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomEditorCommand.class);

    private final ApplicationContext applicationContext;

    @Autowired
    public RoomEditorCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);

        this.applicationContext = applicationContext;

        addSyntax();          // edit the room you're standing in
        //addSyntax(DIRECTION); // TODO edit the room in a direction from here (create it if it doesn't exist)
        addSyntax(ROOM_ID);   // edit room by ID
        addSyntax(NUMBER);    // edit a new room ID
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Output output) {
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

                webSocketContext.getAttributes().put(REDIT_MODEL, room.getId());
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
            room = ch.getLocation().getRoom();
            webSocketContext.getAttributes().put(REDIT_MODEL, room.getId());
        }

        getCommService().sendToRoom(ch.getLocation().getRoom().getId(),
            new Output("[yellow]%s begins editing.", ch.getCharacter().getName()), ch);

        return applicationContext.getBean("roomEditorQuestion", Question.class);
    }

    @Override
    public Question executeBinding(Question question, WebSocketContext webSocketContext, List<Binding> bindings, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);
        MudRoom room;

        if (bindings.size() == 1) {
            room = ch.getLocation().getRoom();
        } else if (ROOM_ID == bindings.get(1).getType()) {
            room = bindings.get(1).asRoom();
        } else if (NUMBER == bindings.get(1).getType()) {
            try {
                Long roomId = Long.parseLong(bindings.get(1).asString());
                Optional<MudRoom> roomOptional = getRepositoryBundle().getRoomRepository().findById(roomId);

                if (roomOptional.isEmpty()) {
                    String roomIdString = bindings.get(1).asString();
                    Long zoneId = Long.parseLong(roomIdString.substring(0, roomIdString.length() - 2));

                    LOGGER.info("Creating room {} in zone {}", roomId, zoneId);

                    room = new MudRoom();

                    room.setId(roomId);
                    room.setZoneId(zoneId);
                    room.setName("Unfinished Room");
                    room.setDescription("A newly constructed room. It still needs a coat of paint.");

                    getRepositoryBundle().getRoomRepository().save(room);
                } else {
                    // this shouldn't really ever happen since the binding was already evaluated as a number and not a room
                    LOGGER.warn("Fetched room from repository by ID instead of from binding: {}", roomId);
                    room = roomOptional.get();
                }
            } catch (NumberFormatException e) {
                // this shouldn't really ever happen either since the binding was already evaluated as a number
                LOGGER.error("Unable to parse room number: {}", e.getMessage());
                output.append("[red]Unable to parse room number: %s", e.getMessage());
                return question;
            }
        } else {
            LOGGER.error("Unrecognized binding type: {}", bindings.get(1).getType());
            output.append("[red]Unrecognized binding type: %s", bindings.get(1).getType());
            return question;
        }

        webSocketContext.getAttributes().put(REDIT_MODEL, room.getId());

        getCommService().sendToRoom(ch.getLocation().getRoom().getId(),
            new Output("[yellow]%s begins editing.", ch.getCharacter().getName()), ch);

        return applicationContext.getBean("roomEditorQuestion", Question.class);
    }
}
