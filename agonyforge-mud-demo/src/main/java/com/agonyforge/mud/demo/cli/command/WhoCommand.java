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

import static com.agonyforge.mud.models.dynamodb.impl.Constants.TYPE_PC;

@Component
public class WhoCommand extends AbstractCommand {
    @Autowired
    public WhoCommand(RepositoryBundle repositoryBundle, CommService commService) {
        super(repositoryBundle, commService);
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
        List<MudCharacter> characters = getRepositoryBundle().getCharacterRepository().getByType(TYPE_PC)
            .stream()
            .filter(ch -> !ch.isPrototype())
            .toList();

        output
            .append("[black]=== [white]Who is Playing [black]===")
            .append("");

        characters.forEach(ch -> output.append("[dwhite]%s", ch.getName()));

        output
            .append("")
            .append("[white]%d player%s online.",
                characters.size(),
                characters.size() == 1 ? "" : "s");

        return question;
    }
}
