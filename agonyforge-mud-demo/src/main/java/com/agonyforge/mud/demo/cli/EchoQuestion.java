package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.core.cli.AbstractQuestion;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.service.EchoService;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.Session;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;

@Component
public class EchoQuestion extends AbstractQuestion {
    private static final Logger LOGGER = LoggerFactory.getLogger(EchoQuestion.class);
    private final EchoService echoService;
    private final MudCharacterRepository characterRepository;

    @Autowired
    public EchoQuestion(EchoService echoService,
                        MudCharacterRepository characterRepository) {
        this.echoService = echoService;
        this.characterRepository = characterRepository;
    }

    @Override
    public Output prompt(Principal principal, Session httpSession) {
        UUID chId = httpSession.getAttribute(MUD_CHARACTER);
        Optional<MudCharacter> chOptional = characterRepository.getById(chId);

        if (chOptional.isEmpty()) {
            LOGGER.error("No character {} found in database!", chId);
            return new Output("", "[default]> ");
        }

        return new Output("", String.format("[green]%s[default]> ", chOptional.get().getName()));
    }

    @Override
    public Response answer(Principal principal, Session httpSession, Input input) {
        if (input.getInput().isBlank()) {
            return new Response(this, new Output("[default]What would you like to say?"));
        }

        UUID chId = httpSession.getAttribute(MUD_CHARACTER);
        Optional<MudCharacter> chOptional = characterRepository.getById(chId);

        if (chOptional.isEmpty()) {
            LOGGER.error("No character {} found in database!", chId);
            return new Response(this, new Output("[red]Unable to fetch your character from the database!"));
        }

        MudCharacter ch = chOptional.get();

        echoService.echoToAll(principal, new Output(String.format("[cyan]%s says, '%s[cyan]'", ch.getName(), input.getInput())));
        return new Response(this, new Output("[cyan]You say, '" + input.getInput() + "[cyan]'"));
    }
}
