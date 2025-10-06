package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.question.login.CharacterSheetFormatter;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScoreCommand extends AbstractCommand {
    private final CharacterSheetFormatter characterSheetFormatter;

    @Autowired
    public ScoreCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext, CharacterSheetFormatter characterSheetFormatter) {
        super(repositoryBundle, commService, applicationContext);
        this.characterSheetFormatter = characterSheetFormatter;

        addSyntax();
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);

        characterSheetFormatter.format(ch, output);

        return question;
    }
}
