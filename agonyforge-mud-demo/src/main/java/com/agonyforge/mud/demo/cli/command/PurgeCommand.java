package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudItem;
import com.agonyforge.mud.demo.service.CommService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.agonyforge.mud.demo.cli.TokenType.*;

@Component
public class PurgeCommand extends AbstractCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(PurgeCommand.class);

    @Autowired
    public PurgeCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);

        addSyntax(ITEM_GROUND);
        addSyntax(ITEM_HELD);
        addSyntax(NPC_IN_ROOM);
    }

    @Override
    public Question executeBinding(Question question, WebSocketContext webSocketContext, List<Binding> bindings, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);
        MudCharacter targetCh = null;
        MudItem targetItem = null;

        if (NPC_IN_ROOM == bindings.get(1).getType()) {
            targetCh = bindings.get(1).asCharacter();
        } else if (ITEM_HELD == bindings.get(1).getType() || ITEM_GROUND == bindings.get(1).getType()) {
            targetItem = bindings.get(1).asItem();
        } else {
            LOGGER.error("Unexpected binding type: {}", bindings.get(1).getType());
            output.append("[red]Binding returned unexpected type: %s", bindings.get(1).getType().name());
        }

        if (targetItem != null) {
            getRepositoryBundle().getItemRepository().delete(targetItem);

            output.append("[yellow]You snap your fingers, and %s disappears!", targetItem.getItem().getShortDescription());
            getCommService().sendToRoom(ch.getLocation().getRoom().getId(),
                new Output("[yellow]%s snaps %s fingers, and %s disappears!",
                    ch.getCharacter().getName(),
                    ch.getCharacter().getPronoun().getPossessive(),
                    targetItem.getItem().getShortDescription()), ch);
        } else if (targetCh != null) {
            getRepositoryBundle().getCharacterRepository().delete(targetCh);

            output.append("[yellow]You snap your fingers, and %s disappears!", targetCh.getCharacter().getName());
            getCommService().sendToRoom(ch.getLocation().getRoom().getId(),
                new Output("[yellow]%s snaps %s fingers, and %s disappears!",
                    ch.getCharacter().getName(),
                    ch.getCharacter().getPronoun().getPossessive(),
                    targetCh.getCharacter().getName()), ch);
        } else {
            LOGGER.error("This should never happen.");
            output.append("[red]Did not find item or character to purge.");
        }

        return question;
    }
}
