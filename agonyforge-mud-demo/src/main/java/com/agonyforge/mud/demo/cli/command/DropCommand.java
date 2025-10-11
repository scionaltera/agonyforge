package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.constant.WearSlot;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudItem;
import com.agonyforge.mud.demo.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static com.agonyforge.mud.demo.cli.TokenType.ITEM_HELD;

@Component
public class DropCommand extends AbstractCommand {
    @Autowired
    public DropCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);

        addSyntax(ITEM_HELD);
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);

        if (tokens.size() == 1) {
            output.append("[default]What would you like to drop?");
            return question;
        }

        Optional<MudItem> targetOptional = findInventoryItem(ch, tokens.get(1));

        if (targetOptional.isEmpty()) {
            output.append("[default]You don't have anything like that.");
            return question;
        }

        MudItem target = targetOptional.get();

        if (!target.getLocation().getWorn().isEmpty()) {
            output.append("[default]You need to remove it first.");
            return question;
        }

        target.getLocation().setWorn(EnumSet.noneOf(WearSlot.class));
        target.getLocation().setHeld(null);
        target.getLocation().setRoom(ch.getLocation().getRoom());
        getRepositoryBundle().getItemRepository().save(target);

        output.append("[default]You drop %s[default].", target.getItem().getShortDescription());
        getCommService().sendToRoom(ch.getLocation().getRoom().getId(),
            new Output("[default]%s drops %s[default].",
                ch.getCharacter().getName(),
                target.getItem().getShortDescription()),
            ch);

        return question;
    }
}
