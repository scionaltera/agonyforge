package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.service.EchoService;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;

import java.security.Principal;

public class EchoQuestion implements Question {
    private final EchoService echoService;

    public EchoQuestion(EchoService echoService) {
        this.echoService = echoService;
    }
    @Override
    public Output prompt(Principal principal) {
        return new Output("", "[default]> ");
    }

    @Override
    public Response answer(Principal principal, Input input) {
        if (input.getInput().isBlank()) {
            return new Response(this, new Output("[default]What would you like to say?"));
        }

        echoService.echo(principal, new Output(String.format("[cyan]%s says, '%s[cyan]'", principal.getName(), input.getInput())));

        return new Response(this, new Output("[cyan]You say, '" + input.getInput() + "[cyan]'"));
    }
}
