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

import static com.agonyforge.mud.demo.cli.TokenType.CHARACTER_IN_ROOM;
import static com.agonyforge.mud.demo.cli.TokenType.ROOM_ID;

@Component
public class TeleportCommand extends AbstractCommand {
    private final SessionAttributeService sessionAttributeService;

    @Autowired
    public TeleportCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext, SessionAttributeService sessionAttributeService) {
        super(repositoryBundle, commService, applicationContext);
        this.sessionAttributeService = sessionAttributeService;

        addSyntax(CHARACTER_IN_ROOM, ROOM_ID);
    }

    @Override
    public Question executeBinding(Question question, WebSocketContext webSocketContext, List<Binding> bindings, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);
        MudCharacter target = bindings.get(1).asCharacter();
        MudRoom destination = bindings.get(2).asRoom();

        Output targetOutput = new Output();

        output.append("[yellow]You TELEPORT %s!", target.getCharacter().getName());
        getCommService().sendToRoom(target.getLocation().getRoom().getId(),
            new Output("[yellow]%s disappears in a puff of smoke!", target.getCharacter().getName()), ch, target);

        target.getLocation().setRoom(destination);
        getRepositoryBundle().getCharacterRepository().save(target);

        getCommService().sendToRoom(target.getLocation().getRoom().getId(),
            new Output("[yellow]%s appears in a puff of smoke!", target.getCharacter().getName()), ch, target);

        targetOutput.append(new Output("[yellow]%s TELEPORTS you!", ch.getCharacter().getName()));
        targetOutput.append(LookCommand.doLook(getRepositoryBundle(), sessionAttributeService, target, target.getLocation().getRoom()));

        getCommService().sendTo(target, targetOutput);

        return question;
    }
}
