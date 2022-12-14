package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CharacterNameQuestion extends DemoQuestion {
    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterNameQuestion.class);

    @Autowired
    public CharacterNameQuestion(ApplicationContext applicationContext,
                                 MudCharacterRepository characterRepository,
                                 MudItemRepository itemRepository) {
        super(applicationContext, characterRepository, itemRepository);
    }

    @Override
    public Output prompt(WebSocketContext wsContext) {
        return new Output("[default]By what name do you wish to be known? ");
    }

    @Override
    public Response answer(WebSocketContext wsContext, Input input) {
        if (input.getInput().isBlank() || input.getInput().length() < 2) {
            return new Response(this, new Output("[red]Names need to be at least two letters in length."));
        } else if (input.getInput().length() > 12) {
            return new Response(this, new Output("[red]Names need to be 12 or fewer letters in length."));
        } else if (!input.getInput().matches("[A-Za-z]+")) {
            return new Response(this, new Output("[red]Names may only have letters in them."));
        } else if (!input.getInput().matches("[A-Z][A-Za-z]+")) {
            return new Response(this, new Output("[red]Names must begin with a capital letter."));
        }

        MudCharacter ch = new MudCharacter();

        ch.setId(UUID.randomUUID());
        ch.setUser(wsContext.getPrincipal().getName());
        ch.setName(input.getInput());

        getCharacterRepository().save(ch);

        Question nextQuestion = getQuestion("characterMenuQuestion");

        return new Response(nextQuestion, new Output(String.format("[default]Hello, [white]%s[default]!", ch.getName())));
    }
}
