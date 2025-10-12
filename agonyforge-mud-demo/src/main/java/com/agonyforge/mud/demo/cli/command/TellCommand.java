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

import static com.agonyforge.mud.demo.cli.TokenType.PLAYER_IN_WORLD;

@Component
public class TellCommand extends AbstractCommand {
    @Autowired
    public TellCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);

        addSyntax(PLAYER_IN_WORLD, TokenType.QUOTED_WORDS);
    }

    @Override
    public Question execute(Question question,
                            WebSocketContext webSocketContext,
                            List<String> tokens,
                            Output output) {

        if (tokens.size() == 1) {
            output.append("[default]Who would you like to tell?");
            return question;
        }

        if (tokens.size() == 2) {
            output.append("[default]What would you like to tell them?");
            return question;
        }

        String targetName = tokens.get(1);
        String message = tokens.get(2);
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);
        Optional<MudCharacter> targetOptional = findWorldCharacter(ch, targetName);

        if (targetOptional.isEmpty()) {
            output.append("[default]There isn't anyone by that name.");
            return question;
        }

        MudCharacter target = targetOptional.get();

        output.append("[red]You tell %s, '%s[red]'", target.getCharacter().getName(), message);
        getCommService().sendTo(target, new Output("[red]%s tells you, '%s[red]'", ch.getCharacter().getName(), message));

        return question;
    }

    @Override
    public Question executeBinding(Question question, WebSocketContext webSocketContext, List<Binding> bindings, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);
        MudCharacter target = bindings.get(1).asCharacter();
        String message = bindings.get(2).asString();

        output.append("[red]You tell %s, '%s[red]'", target.getCharacter().getName(), message);
        getCommService().sendTo(target, new Output("[red]%s tells you, '%s[red]'", ch.getCharacter().getName(), message));

        return question;
    }
}
