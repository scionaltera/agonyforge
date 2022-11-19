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
public class CharacterDeleteQuestion extends DemoQuestion {
    @Autowired
    public CharacterDeleteQuestion(ApplicationContext applicationContext,
                                   MudCharacterRepository characterRepository) {
        super(applicationContext, characterRepository);
    }

    @Override
    public Output prompt(Principal principal, Session session) {
        Output output = new Output();
        Optional<MudCharacter> chOptional = getCharacter(session, output);

        return chOptional.map(mudCharacter -> new Output(
            String.format(
                "[red]Are you SURE you want to delete %s? [white][y/N][red]: ",
                mudCharacter.getName())))
            .orElse(output);
    }

    @Override
    public Response answer(Principal principal, Session session, Input input) {
        Output output = new Output();
        Question next = this;
        Optional<MudCharacter> chOptional = getCharacter(session, output);

        if (chOptional.isPresent()) {
            if ("Y".equalsIgnoreCase(input.getInput())) {
                getCharacterRepository().delete(chOptional.get());
                output.append("[red]Your character has been deleted.");
                next = getQuestion("characterMenuQuestion");
            } else {
                output.append("[green]Ok! Your character is safe!");
            }
        }

        return new Response(next, output);
    }
}
