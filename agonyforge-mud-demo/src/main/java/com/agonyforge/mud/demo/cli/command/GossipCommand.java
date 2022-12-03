package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import com.agonyforge.mud.models.dynamodb.service.CommService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class GossipCommand implements Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(GossipCommand.class);
    private final MudCharacterRepository characterRepository;
    private final CommService commService;

    @Autowired
    public GossipCommand(MudCharacterRepository characterRepository,
                         CommService commService) {
        this.characterRepository = characterRepository;
        this.commService = commService;
    }

    @Override
    public Question execute(Question question,
                            WebSocketContext webSocketContext,
                            List<String> tokens,
                            Input input,
                            Output output) {
        String message = Command.stripFirstWord(input.getInput());

        if (message.isBlank()) {
            output.append("[default]What would you like to gossip?");
            return question;
        }

        Optional<MudCharacter> chOptional = Command.getCharacter(characterRepository, webSocketContext, output);

        if (chOptional.isPresent()) {
            MudCharacter ch = chOptional.get();

            output.append("[green]You gossip, '" + message + "[green]'");
            commService.sendToAll(webSocketContext, new Output(String.format("[green]%s gossips, '%s[green]'", ch.getName(), message)));
        }

        return question;
    }
}
