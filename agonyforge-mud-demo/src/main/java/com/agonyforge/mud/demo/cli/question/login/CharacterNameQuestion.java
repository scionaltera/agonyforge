package com.agonyforge.mud.demo.cli.question.login;

import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.cli.question.BaseQuestion;
import com.agonyforge.mud.demo.model.constant.WearSlot;
import com.agonyforge.mud.demo.model.constant.Pronoun;
import com.agonyforge.mud.demo.model.impl.MudCharacterPrototype;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_PCHARACTER;

@Component
public class CharacterNameQuestion extends BaseQuestion {
    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterNameQuestion.class);

    @Autowired
    public CharacterNameQuestion(ApplicationContext applicationContext,
                                 RepositoryBundle repositoryBundle) {
        super(applicationContext, repositoryBundle);
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

        MudCharacterPrototype ch = new MudCharacterPrototype();

        ch.setUsername(wsContext.getPrincipal().getName());
        ch.setName(input.getInput());
        ch.setPronoun(Pronoun.THEY);
        ch.setWearSlots(Arrays.stream(WearSlot.values()).toList());

        ch = getRepositoryBundle().getCharacterPrototypeRepository().save(ch);
        wsContext.getAttributes().put(MUD_PCHARACTER, ch.getId());

        LOGGER.info("New character created: {}", ch.getName());

        Question nextQuestion = getQuestion("characterPronounQuestion");

        return new Response(nextQuestion, new Output("[default]Hello, [white]%s[default]!", ch.getName()));
    }
}
