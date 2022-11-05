package com.agonyforge.mud.cli.demo;

import com.agonyforge.mud.cli.Question;
import com.agonyforge.mud.cli.Response;
import com.agonyforge.mud.web.model.Input;
import com.agonyforge.mud.web.model.Output;

public class EchoQuestion implements Question {
    @Override
    public Output prompt() {
        return new Output("", "[default]> ");
    }

    @Override
    public Response answer(Input input) {
        if (input.getInput().isBlank()) {
            return new Response(this, new Output("[default]What would you like to say?"));
        }

        return new Response(this, new Output("[cyan]You say, '" + input.getInput() + "[cyan]'"));
    }
}
