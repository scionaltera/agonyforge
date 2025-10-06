package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudCharacterTemplate;
import com.agonyforge.mud.demo.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.agonyforge.mud.demo.cli.command.TokenType.NPC_ID;

@Component
public class SpawnCommand extends AbstractCommand {
    @Autowired
    public SpawnCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);

        addSyntax(NPC_ID);
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);

        if (tokens.size() != 2) {
            output.append("[default]What is the ID of the creature you'd like to create?");
            return question;
        }

        Optional<MudCharacterTemplate> npcTemplate = Optional.empty();

        try {
            Long id = Long.parseLong(tokens.get(1));
            npcTemplate = getRepositoryBundle().getCharacterPrototypeRepository().findById(id);
        } catch (NumberFormatException e) {
            // TODO search for template by name
        }

        if (npcTemplate.isEmpty()) {
            output.append("[red]There is no creature with that ID.");
            return question;
        }

        MudCharacter npc = npcTemplate.get().buildInstance();
        npc.getLocation().setRoom(ch.getLocation().getRoom());
        npc = getRepositoryBundle().getCharacterRepository().save(npc);

        output.append("[yellow]You wave your hand, and %s appears!", npc.getCharacter().getName());
        getCommService().sendToRoom(ch.getLocation().getRoom().getId(),
            new Output("[yellow]%s waves %s hand, and %s appears!",
                ch.getCharacter().getName(),
                ch.getCharacter().getPronoun().getPossessive(),
                npc.getCharacter().getName()), ch);

        return question;
    }
}
