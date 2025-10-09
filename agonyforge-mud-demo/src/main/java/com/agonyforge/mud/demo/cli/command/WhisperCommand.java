package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
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
                            List<String> tokens,
                            Output output) {

        if (tokens.size() == 1) {
            output.append("[default]Who would you like to whisper to?");
            return question;
        }

        if (tokens.size() == 2) {
            output.append("[default]What would you like to whisper to them?");
            return question;
        }

        String message = tokens.get(2);
        String targetName = tokens.get(1);
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);
        Optional<MudCharacter> targetOptional = findRoomCharacter(ch, targetName);

        if (targetOptional.isEmpty()) {
            output.append("[default]There isn't anyone by that name.");
            return question;
        }

        MudCharacter target = targetOptional.get();

        output.append("[red]You whisper to %s, '%s[red]'", target.getCharacter().getName(), message);
        getCommService().sendTo(target, new Output("[red]%s whispers to you, '%s[red]'", ch.getCharacter().getName(), message));
        getCommService().sendToRoom(
            ch.getLocation().getRoom().getId(),
            new Output("[red]%s whispers something to %s.", ch.getCharacter().getName(), target.getCharacter().getName()),
            ch, target);

        return question;
    }
}
