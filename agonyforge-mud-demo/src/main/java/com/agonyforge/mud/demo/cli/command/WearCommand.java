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
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class WearCommand extends AbstractCommand {
    @Autowired
    public WearCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);

        if (tokens.size() == 1) {
            output.append("[default]What would you like to wear?");
            return question;
        }

        Optional<MudItem> targetOptional = findInventoryItem(ch, tokens.get(1));

        if (targetOptional.isEmpty()) {
            output.append("[default]You aren't carrying anything like that.");
            return question;
        }

        MudItem target = targetOptional.get();

        if (target.getItem().getWearSlots().isEmpty()) {
            output.append("[default]You can't wear that.");
            return question;
        }

        if (target.getLocation().getWorn() != null) {
            LOGGER.error("Item in inventory is also being worn! instance:{} proto:{}", target.getInstanceId(), target.getId());
            output.append("[default]You are already wearing %s[default].", target.getItem().getShortDescription());
            return question;
        }

        // find all items already worn
        List<MudItem> wornItems = getRepositoryBundle().getItemRepository().findByLocationHeld(ch)
            .stream()
            .filter(item -> item.getLocation().getWorn() != null)
            .toList();

        // find all slots that the character has AND the target item has AND isn't already occupied
        List<WearSlot> candidateSlots = target.getItem().getWearSlots()
            .stream()
            .filter(slot -> ch.getCharacter().getWearSlots().contains(slot))
            .filter(slot -> wornItems.stream().noneMatch(item -> item.getLocation().getWorn().equals(slot)))
            .toList();

        if (candidateSlots.isEmpty()) {
            output.append("[default]You need to take something else off before you can wear that.");
            return question;
        }

        WearSlot targetSlot = candidateSlots.get(0);
        target.getLocation().setWorn(targetSlot);
        target.getLocation().setHeld(ch);
        target.getLocation().setRoom(null);
        getRepositoryBundle().getItemRepository().save(target);

        output.append("[default]You wear %s[default] on your %s.", target.getItem().getShortDescription(), targetSlot.getName());
        getCommService().sendToRoom(webSocketContext, ch.getLocation().getRoom().getId(),
            new Output("[default]%s wears %s[default] on %s %s.",
                ch.getCharacter().getName(),
                target.getItem().getShortDescription(),
                ch.getCharacter().getPronoun().getPossessive(),
                targetSlot
            ));

        return question;
    }
}
