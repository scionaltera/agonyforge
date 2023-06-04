package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScoreCommand extends AbstractCommand {
    @Autowired
    public ScoreCommand(RepositoryBundle repositoryBundle, CommService commService) {
        super(repositoryBundle, commService);
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);

        output.append("[default]Your name is [white]%s[default].", ch.getName());
        output.append("[default]Your pronouns are [white]%s/%s[default].", ch.getPronoun().getSubject(), ch.getPronoun().getObject());
        output.append("[default]STR: [white]%s", ch.getStrength());
        output.append("[default]DEX: [white]%s", ch.getDexterity());
        output.append("[default]CON: [white]%s", ch.getConstitution());
        output.append("[default]INT: [white]%s", ch.getIntelligence());
        output.append("[default]WIS: [white]%s", ch.getWisdom());
        output.append("[default]CHA: [white]%s", ch.getCharisma());
        output.append("[default]DEF: [white]%s", ch.getDefense());

        return question;
    }
}
