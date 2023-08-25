package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.constant.WearSlot;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudItem;
import com.agonyforge.mud.demo.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RemoveCommand extends AbstractCommand {
    @Autowired
    public RemoveCommand(RepositoryBundle repositoryBundle, CommService commService) {
        super(repositoryBundle, commService);
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);

        if (tokens.size() == 1) {
            output.append("[default]What would you like to remove?");
            return question;
        }

        Optional<MudItem> targetOptional = findInventoryItem(ch, tokens.get(1));

        if (targetOptional.isEmpty()) {
            output.append("[default]You aren't wearing anything like that.");
            return question;
        }

        MudItem target = targetOptional.get();

        if (target.getWorn() == null) {
            output.append("[default]You aren't wearing %s[default].", target.getShortDescription());
            return question;
        }

        WearSlot targetSlot = target.getWorn();
        target.setWorn(null);
        getRepositoryBundle().getItemRepository().save(target);

        output.append("[default]You remove %s[default].", target.getShortDescription());
        getCommService().sendToRoom(webSocketContext, ch.getRoomId(),
            new Output("[default]%s removes %s[default] from %s %s.",
                ch.getName(),
                target.getShortDescription(),
                ch.getPronoun().getPossessive(),
                targetSlot.getName()
            ));

        return question;
    }
}
