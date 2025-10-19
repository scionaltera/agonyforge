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
public class WhisperCommand extends AbstractCommand {
    @Autowired
    public WhisperCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);

        addSyntax(TokenType.CHARACTER_IN_ROOM, TokenType.QUOTED_WORDS);
    }

    @Override
    public Question execute(Question question,
                            WebSocketContext webSocketContext,
                            List<Binding> bindings,
                            Output output) {

        MudCharacter ch = getCurrentCharacter(webSocketContext, output);
        MudCharacter target = bindings.get(1).asCharacter();
        String message = bindings.get(2).asString();

        output.append("[red]You whisper to %s, '%s[red]'", target.getCharacter().getName(), message);
        getCommService().sendTo(target, new Output("[red]%s whispers to you, '%s[red]'", ch.getCharacter().getName(), message));
        getCommService().sendToRoom(
            ch.getLocation().getRoom().getId(),
            new Output("[red]%s whispers something to %s.", ch.getCharacter().getName(), target.getCharacter().getName()),
            ch, target);

        return question;
    }
}
