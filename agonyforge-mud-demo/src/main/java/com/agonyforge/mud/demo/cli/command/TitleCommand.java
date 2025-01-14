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

@Component
public class TitleCommand extends AbstractCommand {
    @Autowired
    public TitleCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
        String title = Command.stripFirstWord(input.getInput());
        String titleColorless = Command.stripColors(title);

        if (titleColorless.length() > 60) {
            output.append("[default]That title is too long, please try something shorter.");
            return question;
        }

        MudCharacter ch = getCurrentCharacter(webSocketContext, output);

        ch.getPlayer().setTitle(title);
        getRepositoryBundle().getCharacterRepository().save(ch);

        output.append("[default]Changed title to: [white]%s %s", ch.getCharacter().getName(), title);

        return question;
    }
}
