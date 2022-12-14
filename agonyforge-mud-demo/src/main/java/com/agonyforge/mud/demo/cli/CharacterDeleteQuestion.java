package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CharacterDeleteQuestion extends DemoQuestion {
    @Autowired
    public CharacterDeleteQuestion(ApplicationContext applicationContext,
                                   MudCharacterRepository characterRepository,
                                   MudItemRepository itemRepository) {
        super(applicationContext, characterRepository, itemRepository);
    }

    @Override
    public Output prompt(WebSocketContext wsContext) {
        Output output = new Output();
        Optional<MudCharacter> chOptional = getCharacter(wsContext, output);

        return chOptional.map(mudCharacter -> new Output(
            String.format(
                "[red]Are you SURE you want to delete %s? [white][y/N][red]: ",
                mudCharacter.getName())))
            .orElse(output);
    }

    @Override
    public Response answer(WebSocketContext wsContext, Input input) {
        Output output = new Output();
        Question next = this;
        Optional<MudCharacter> chOptional = getCharacter(wsContext, output);

        if (chOptional.isPresent()) {
            if ("Y".equalsIgnoreCase(input.getInput())) {
                getCharacterRepository().delete(chOptional.get());
                output.append("[red]Your character has been deleted.");
            } else {
                output.append("[green]Ok! Your character is safe!");
            }

            next = getQuestion("characterMenuQuestion");
        }

        return new Response(next, output);
    }
}
