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

import static com.agonyforge.mud.demo.cli.TokenType.QUOTED_WORDS;

@Component
public class EmoteCommand extends AbstractCommand {
    @Autowired
    public EmoteCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);

        addSyntax(QUOTED_WORDS);
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Output output) {
        String message = tokens.get(1);

        if (message.isBlank()) {
            output.append("[default]What would you like to emote?");
            return question;
        }

        MudCharacter ch = getCurrentCharacter(webSocketContext, output);
        Output formatted = new Output("[dcyan]%s %s", ch.getCharacter().getName(), message);

        output.append(formatted);
        getCommService().sendToRoom(ch.getLocation().getRoom().getId(), formatted, ch);

        return question;
    }
}
