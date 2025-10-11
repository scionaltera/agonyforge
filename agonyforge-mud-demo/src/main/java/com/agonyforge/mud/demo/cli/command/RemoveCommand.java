package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.cli.TokenType;
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

@Component
public class RemoveCommand extends AbstractCommand {
    @Autowired
    public RemoveCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);

        addSyntax(TokenType.ITEM_WORN);
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);

        if (tokens.size() == 1) {
            output.append("[default]What would you like to remove?");
            return question;
        }

        Optional<MudItem> targetOptional = findWornItem(ch, tokens.get(1));

        if (targetOptional.isEmpty()) {
            output.append("[default]You aren't wearing anything like that.");
            return question;
        }

        MudItem target = targetOptional.get();

        if (target.getLocation().getWorn().isEmpty()) {
            output.append("[default]You aren't wearing %s[default].", target.getItem().getShortDescription());
            return question;
        }

        String slotNames = String.join(", ", target.getLocation().getWorn().stream().map(WearSlot::getName).toList());

        target.getLocation().setWorn(EnumSet.noneOf(WearSlot.class));
        target.getLocation().setHeld(ch);
        target.getLocation().setRoom(null);
        getRepositoryBundle().getItemRepository().save(target);

        output.append("[default]You remove %s[default].", target.getItem().getShortDescription());
        getCommService().sendToRoom(ch.getLocation().getRoom().getId(),
            new Output("[default]%s removes %s[default] from %s %s.",
                ch.getCharacter().getName(),
                target.getItem().getShortDescription(),
                ch.getCharacter().getPronoun().getPossessive(),
                slotNames
            ), ch);

        return question;
    }
}
