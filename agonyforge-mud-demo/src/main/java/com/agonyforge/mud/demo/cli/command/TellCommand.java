package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.agonyforge.mud.demo.cli.command.TokenType.CHARACTER_IN_WORLD;

@Component
public class TellCommand extends AbstractCommand {
    @Autowired
    public TellCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);

        addSyntax(CHARACTER_IN_WORLD, TokenType.QUOTED_WORDS);
    }

    @Override
    public Question execute(Question question,
                            WebSocketContext webSocketContext,
                            List<String> tokens,
                            Input input,
                            Output output) {

        if (tokens.size() == 1) {
            output.append("[default]Who would you like to tell?");
            return question;
        }

        if (tokens.size() == 2) {
            output.append("[default]What would you like to tell them?");
            return question;
        }

        String message = Command.stripFirstWord(Command.stripFirstWord(input.getInput()));
        String targetName = tokens.get(1);
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
}
