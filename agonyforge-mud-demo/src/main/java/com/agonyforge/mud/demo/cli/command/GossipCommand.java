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

@Component
public class GossipCommand extends AbstractCommand {
    @Autowired
    public GossipCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);

        addSyntax(TokenType.QUOTED_WORDS);
    }

    @Override
    public Question executeBinding(Question question,
                            WebSocketContext webSocketContext,
                            List<Binding> bindings,
                            Output output) {
        String message = bindings.get(1).asString();
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);

        output.append("[green]You gossip, '%s[green]'", message);
        getCommService().sendToAll(webSocketContext, new Output("[green]%s gossips, '%s[green]'", ch.getCharacter().getName(), message));

        return question;
    }
}
