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
public class QuitCommand extends AbstractCommand {
    @Autowired
    public QuitCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
        if (tokens.size() != 2 || !"QUIT".equals(tokens.get(0)) || !"NOW".equals(tokens.get(1))) {
            output.append("[red]You must type 'quit now'.");
            return question;
        }

        Question characterMenuQuestion = getApplicationContext().getBean("characterMenuQuestion", Question.class);
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);

        output.append("[white]Goodbye!");

        // TODO the following duplicates code in CharacterJanitor and could be consolidated
        ch.setLocation(null);
        getRepositoryBundle().getCharacterRepository().save(ch);

        LOGGER.info("{} has left the game.", ch.getCharacter().getName());

        getCommService().sendToAll(webSocketContext,
            new Output("[yellow]%s has left the game!", ch.getCharacter().getName()), ch);

        return characterMenuQuestion;
    }
}
