package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.service.SessionAttributeService;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.agonyforge.mud.demo.cli.TokenType.CHARACTER_IN_WORLD;

@Component
public class TransferCommand extends AbstractCommand {
    private final SessionAttributeService sessionAttributeService;

    @Autowired
    public TransferCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext, SessionAttributeService sessionAttributeService) {
        super(repositoryBundle, commService, applicationContext);
        this.sessionAttributeService = sessionAttributeService;

        addSyntax(CHARACTER_IN_WORLD);
    }

    @Override
    public Question executeBinding(Question question, WebSocketContext webSocketContext, List<Binding> bindings, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);
        MudCharacter target = bindings.get(1).asCharacter();

        if (target.getLocation() != null && target.getLocation().getRoom().equals(ch.getLocation().getRoom())) {
            output.append("[red]%s %s already here in the room with you.",
                StringUtils.capitalize(target.getCharacter().getPronoun().getSubject()),
                target.getCharacter().getPronoun().isPlural() ? "are" : "is");
            return question;
        }

        Output targetOutput = new Output();

        output.append("[yellow]You TRANSFER %s!", target.getCharacter().getName());
        getCommService().sendToRoom(target.getLocation().getRoom().getId(),
            new Output("[yellow]%s disappears in a puff of smoke!", target.getCharacter().getName()), ch, target);

        target.getLocation().setRoom(ch.getLocation().getRoom());
        getRepositoryBundle().getCharacterRepository().save(target);

        getCommService().sendToRoom(target.getLocation().getRoom().getId(),
            new Output("[yellow]%s appears in a puff of smoke!", target.getCharacter().getName()), ch, target);

        targetOutput.append(new Output("[yellow]%s TRANSFERS you!", ch.getCharacter().getName()));
        targetOutput.append(LookCommand.doLook(getRepositoryBundle(), sessionAttributeService, target, target.getLocation().getRoom()));

        getCommService().sendTo(target, targetOutput);

        return question;
    }
}
