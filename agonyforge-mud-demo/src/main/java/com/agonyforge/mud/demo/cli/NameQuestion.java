package com.agonyforge.mud.demo.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.agonyforge.mud.core.cli.AbstractQuestion;
import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.session.Session;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class NameQuestion extends AbstractQuestion {
    public static final String NAME_KEY = "MUD.NAME";

    private static final Logger LOGGER = LoggerFactory.getLogger(NameQuestion.class);

    private final Question nextQuestion;

    @Autowired
    public NameQuestion(@Qualifier("echoQuestion") Question nextQuestion) {
        this.nextQuestion = nextQuestion;
    }

    @Override
    public Output prompt(Principal principal, Session httpSession) {
        return new Output("[default]By what name do you wish to be known? ");
    }

    @Override
    public Response answer(Principal principal, Session httpSession, Input input) {
        if (input.getInput().isBlank() || input.getInput().length() < 2) {
            return new Response(this, new Output("[red]Names need to be at least two letters in length."));
        } else if (input.getInput().length() > 12) {
            return new Response(this, new Output("[red]Names need to be 12 or fewer letters in length."));
        } else if (input.getInput().matches("[^A-Za-z]+")) {
            return new Response(this, new Output("[red]Names may only have letters in them."));
        }

        httpSession.setAttribute(NAME_KEY, input.getInput());

        LOGGER.info("{} is now known as {}", principal.getName(), input.getInput());

        return new Response(nextQuestion, new Output(String.format("[default]Hello, [white]%s[default]!", input.getInput())));
    }
}
