package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.session.Session;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Optional;

@Component
public class CharacterViewQuestion extends DemoQuestion {
    @Autowired
    public CharacterViewQuestion(ApplicationContext applicationContext,
                                 MudCharacterRepository characterRepository) {
        super(applicationContext, characterRepository);
    }

    @Override
    public Output prompt(Principal principal, Session session) {
        Output output = new Output();
        Optional<MudCharacter> chOptional = getCharacter(session, output);

        if (chOptional.isPresent()) {
            output.append("[dcyan]Character Sheet");
            output.append(String.format("[default]Name: [cyan]%s", chOptional.get().getName()));
            output.append("");
            output.append("[green]P[dwhite]) Play as this character");
            output.append("[red]D[dwhite]) Delete this character");
            output.append("[dwhite]Please [white]make your selection[dwhite]: ");
        }

        return output;
    }

    @Override
    public Response answer(Principal principal, Session session, Input input) {
        Output output = new Output();
        Question next = this;

        if ("P".equalsIgnoreCase(input.getInput())) {
            next = getQuestion("echoQuestion");
        } else if ("D".equalsIgnoreCase(input.getInput())) {
            next = getQuestion("characterDeleteQuestion");
        } else {
            output.append("[red]Unknown selection. Please try again.");
        }

        return new Response(next, output);
    }
}
