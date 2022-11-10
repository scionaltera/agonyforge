package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.core.cli.AbstractQuestion;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.service.EchoService;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Map;

@Component
public class EchoQuestion extends AbstractQuestion {
    private final EchoService echoService;

    @Autowired
    public EchoQuestion(EchoService echoService) {
        this.echoService = echoService;
    }

    @Override
    public Output prompt(Principal principal, Map<String, Object> stompSession) {
        return new Output("", String.format("[green]%s[default]> ", principal.getName()));
    }

    @Override
    public Response answer(Principal principal, Map<String, Object> stompSession, Input input) {
        if (input.getInput().isBlank()) {
            return new Response(this, new Output("[default]What would you like to say?"));
        }

        echoService.echoToAll(principal, new Output(String.format("[cyan]%s says, '%s[cyan]'", principal.getName(), input.getInput())));

        return new Response(this, new Output("[cyan]You say, '" + input.getInput() + "[cyan]'"));
    }
}
