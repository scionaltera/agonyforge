package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudItem;
import com.agonyforge.mud.demo.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.agonyforge.mud.demo.cli.command.TokenType.*;

@Component
public class PurgeCommand extends AbstractCommand {
    static {
        addSyntax(ITEM_GROUND);
        addSyntax(ITEM_HELD);
        addSyntax(CHARACTER_IN_ROOM);
    }

    @Autowired
    public PurgeCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);

        if (tokens.size() != 2) {
            output.append("[default]What would you like to purge?");
            return question;
        }

        Optional<MudItem> targetOptional = findInventoryItem(ch, tokens.get(1));
        Optional<MudCharacter> targetOptionalCh = Optional.empty();

        if (targetOptional.isEmpty()) {
            targetOptional = findRoomItem(ch, tokens.get(1));
        }

        if (targetOptional.isEmpty()) {
            targetOptionalCh = findRoomCharacter(ch, tokens.get(1));
        }

        if (targetOptional.isEmpty() && targetOptionalCh.isEmpty()) {
            output.append("[default]You don't see anything like that.");
            return question;
        }

        if (targetOptional.isPresent()) {
            MudItem target = targetOptional.get();
            getRepositoryBundle().getItemRepository().delete(target);

            output.append("[yellow]You snap your fingers, and %s disappears!", target.getItem().getShortDescription());
            getCommService().sendToRoom(ch.getLocation().getRoom().getId(),
                new Output("[yellow]%s snaps %s fingers, and %s disappears!",
                    ch.getCharacter().getName(),
                    ch.getCharacter().getPronoun().getPossessive(),
                    target.getItem().getShortDescription()), ch);
        } else {
            MudCharacter target = targetOptionalCh.get();

            if (target.getPlayer() != null) {
                output.append("[default]Players cannot be purged.");
                return question;
            }

            getRepositoryBundle().getCharacterRepository().delete(target);

            output.append("[yellow]You snap your fingers, and %s disappears!", target.getCharacter().getName());
            getCommService().sendToRoom(ch.getLocation().getRoom().getId(),
                new Output("[yellow]%s snaps %s fingers, and %s disappears!",
                    ch.getCharacter().getName(),
                    ch.getCharacter().getPronoun().getPossessive(),
                    target.getCharacter().getName()), ch);
        }

        return question;
    }
}
