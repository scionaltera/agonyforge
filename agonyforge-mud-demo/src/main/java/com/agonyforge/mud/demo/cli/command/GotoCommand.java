package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.service.SessionAttributeService;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudRoom;
import com.agonyforge.mud.demo.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.agonyforge.mud.demo.cli.TokenType.CHARACTER_IN_WORLD;
import static com.agonyforge.mud.demo.cli.TokenType.ROOM_ID;

@Component
public class GotoCommand extends AbstractCommand {
    private final SessionAttributeService sessionAttributeService;

    @Autowired
    public GotoCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext, SessionAttributeService sessionAttributeService) {
        super(repositoryBundle, commService, applicationContext);
        this.sessionAttributeService = sessionAttributeService;

        addSyntax(ROOM_ID);
        addSyntax(CHARACTER_IN_WORLD);
    }

    @Override
    public Question executeBinding(Question question, WebSocketContext webSocketContext, List<Binding> bindings, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);
        MudCharacter destinationCharacter;
        MudRoom destinationRoom;

        if (CHARACTER_IN_WORLD == bindings.get(1).getType()) {
            destinationCharacter = bindings.get(1).asCharacter();
            destinationRoom = destinationCharacter.getLocation().getRoom();
        } else if (ROOM_ID == bindings.get(1).getType()) {
            destinationRoom = bindings.get(1).asRoom();
        } else {
            output.append("Can't find anything like that to go to.");
            return question;
        }

        getCommService().sendToRoom(ch.getLocation().getRoom().getId(),
            new Output("[yellow]%s disappears in a puff of smoke!", ch.getCharacter().getName()), ch);

        ch.getLocation().setRoom(destinationRoom);
        getRepositoryBundle().getCharacterRepository().save(ch);

        getCommService().sendToRoom(ch.getLocation().getRoom().getId(),
            new Output("[yellow]%s appears in a puff of smoke!", ch.getCharacter().getName()), ch);

        output.append(LookCommand.doLook(getRepositoryBundle(), sessionAttributeService, ch, destinationRoom));

        return question;
    }
}
