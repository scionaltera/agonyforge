package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.service.SessionAttributeService;
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
import java.util.NoSuchElementException;
import java.util.Optional;

@Component
public class TeleportCommand extends AbstractCommand {
    private final SessionAttributeService sessionAttributeService;

    @Autowired
    public TeleportCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext, SessionAttributeService sessionAttributeService) {
        super(repositoryBundle, commService, applicationContext);
        this.sessionAttributeService = sessionAttributeService;
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);

        if (tokens.size() == 1) {
            output.append("[default]Whom do you wish to teleport?");
            return question;
        }

        if (tokens.size() == 2) {
            output.append("[default]Where would you like to send them?");
            return question;
        }

        if (tokens.size() > 3) {
            output.append("[default]TELEPORT &lt;victim&gt; &lt;destination&gt;");
            return question;
        }

        Optional<MudCharacter> targetOptional = findWorldCharacter(ch, tokens.get(1));
        MudRoom destination;

        if (targetOptional.isEmpty() || targetOptional.get().getPlayer() == null) {
            output.append("[red]No such player exists.");
            return question;
        }

        MudCharacter target = targetOptional.get();

        try {
            Long id = Long.parseLong(tokens.get(2));
            destination = getRepositoryBundle().getRoomRepository().findById(id).orElseThrow();
        } catch (NumberFormatException | NoSuchElementException e) {
            output.append("[red]No such room exists.");
            return question;
        }

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
