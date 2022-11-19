package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.service.EchoService;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EchoQuestion extends DemoQuestion {
    private final EchoService echoService;

    @Autowired
    public EchoQuestion(EchoService echoService,
                        ApplicationContext applicationContext,
                        MudCharacterRepository characterRepository) {
        super(applicationContext, characterRepository);
        this.echoService = echoService;
    }

    @Override
    public Output prompt(WebSocketContext wsContext) {
        Output output = new Output();
        Optional<MudCharacter> chOptional = getCharacter(wsContext, output);

        if (chOptional.isPresent()) {
            output.append("", String.format("[green]%s[default]> ", chOptional.get().getName()));
        } else {
            output.append("", "[default]> ");
        }

        return output;
    }

    @Override
    public Response answer(WebSocketContext wsContext, Input input) {
        if (input.getInput().isBlank()) {
            return new Response(this, new Output("[default]What would you like to say?"));
        }

        Output output = new Output();
        Optional<MudCharacter> chOptional = getCharacter(wsContext, output);

        if (chOptional.isPresent()) {
            MudCharacter ch = chOptional.get();

            output.append("[cyan]You say, '" + input.getInput() + "[cyan]'");
            echoService.echoToAll(wsContext, new Output(String.format("[cyan]%s says, '%s[cyan]'", ch.getName(), input.getInput())));
            return new Response(this, output);
        }

        return new Response(this, output);
    }
}
