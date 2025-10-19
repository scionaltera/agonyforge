package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.cli.TokenType;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SlayCommand extends AbstractCommand {
    @Autowired
    public SlayCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);

        addSyntax(TokenType.NPC_IN_ROOM);
    }

    @Override
    public Question executeBinding(Question question, WebSocketContext webSocketContext, List<Binding> bindings, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);
        MudCharacter target = bindings.get(1).asCharacter();
        getRepositoryBundle().getCharacterRepository().delete(target);

        output.append("[black]You snap your fingers, and %s DIES!", target.getCharacter().getName());
        getCommService().sendToRoom(ch.getLocation().getRoom().getId(),
            new Output("[black]%s snaps %s fingers, and %s DIES!",
                ch.getCharacter().getName(),
                ch.getCharacter().getPronoun().getPossessive(),
                target.getCharacter().getName()), ch);

        return question;
    }
}
