package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.service.CommService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.agonyforge.mud.demo.cli.TokenType.WORD;

@Component
public class QuitCommand extends AbstractCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuitCommand.class);

    @Autowired
    public QuitCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);

        addSyntax(WORD);
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Output output) {
        if (tokens.size() != 2 || !"quit".equalsIgnoreCase(tokens.get(0)) || !"now".equals(tokens.get(1))) {
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
