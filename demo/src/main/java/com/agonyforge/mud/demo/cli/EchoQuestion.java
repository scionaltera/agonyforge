package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class EchoQuestion implements Question {
    @Override
    public Output prompt(Principal principal) {
        return new Output("", "[default]> ");
    }

    @Override
    public Response answer(Principal principal, Input input) {
        if (input.getInput().isBlank()) {
            return new Response(this, new Output("[default]What would you like to say?"));
        }

        return new Response(this, new Output("[cyan]You say, '" + input.getInput() + "[cyan]'"));
    }
}
