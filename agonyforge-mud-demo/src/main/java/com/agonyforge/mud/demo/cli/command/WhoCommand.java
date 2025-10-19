package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.Role;
import com.agonyforge.mud.demo.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WhoCommand extends AbstractCommand {
    @Autowired
    public WhoCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);

        addSyntax();
    }

    @Override
    public Question executeBinding(Question question, WebSocketContext webSocketContext, List<Binding> bindings, Output output) {
        List<MudCharacter> characters = getRepositoryBundle().getCharacterRepository().findAll()
            .stream()
            .filter(ch -> ch.getPlayer() != null)
            .filter(ch -> ch.getLocation() != null)
            .toList();

        output
            .append("[black]=== [white]Who is Playing [black]===")
            .append("");

        characters.forEach(ch -> output.append("[%s]%s %s",
            ch.getPlayer().getRoles().stream().anyMatch(Role::isImplementor) ? "yellow" : "white",
            ch.getCharacter().getName(),
            ch.getPlayer().getTitle()));

        output
            .append("")
            .append("[white]%d player%s online.",
                characters.size(),
                characters.size() == 1 ? "" : "s");

        return question;
    }
}
