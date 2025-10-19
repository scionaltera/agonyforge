package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudCharacterTemplate;
import com.agonyforge.mud.demo.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.agonyforge.mud.demo.cli.TokenType.NPC_ID;

@Component
public class SpawnCommand extends AbstractCommand {
    @Autowired
    public SpawnCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);

        addSyntax(NPC_ID);
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<Binding> bindings, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);
        MudCharacterTemplate npcTemplate = bindings.get(1).asCharacterTemplate();
        MudCharacter npc = npcTemplate.buildInstance();
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
