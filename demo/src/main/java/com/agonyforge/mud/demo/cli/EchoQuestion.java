package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.core.cli.AbstractQuestion;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.service.EchoService;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Map;

import static com.agonyforge.mud.demo.cli.NameQuestion.NAME_KEY;

@Component
public class EchoQuestion extends AbstractQuestion {
    private static final Logger LOGGER = LoggerFactory.getLogger(EchoQuestion.class);
    private final EchoService echoService;
    private final FindByIndexNameSessionRepository<Session> sessionRepository;

    @Autowired
    public EchoQuestion(EchoService echoService,
                        FindByIndexNameSessionRepository<Session> sessionRepository) {
        this.echoService = echoService;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public Output prompt(Principal principal, Session httpSession) {
        String name = httpSession.getAttributeOrDefault(NAME_KEY, principal.getName());

        return new Output("", String.format("[green]%s[default]> ", name));
    }

    @Override
    public Response answer(Principal principal, Session httpSession, Input input) {
        if (input.getInput().isBlank()) {
            return new Response(this, new Output("[default]What would you like to say?"));
        }

        String name = httpSession.getAttributeOrDefault(NAME_KEY, principal.getName());

        echoService.echoToAll(principal, new Output(String.format("[cyan]%s says, '%s[cyan]'", name, input.getInput())));

        return new Response(this, new Output("[cyan]You say, '" + input.getInput() + "[cyan]'"));
    }
}
