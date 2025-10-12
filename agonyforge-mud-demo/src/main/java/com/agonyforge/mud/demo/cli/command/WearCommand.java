package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.cli.TokenType;
import com.agonyforge.mud.demo.model.constant.WearMode;
import com.agonyforge.mud.demo.model.constant.WearSlot;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudItem;
import com.agonyforge.mud.demo.service.CommService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class WearCommand extends AbstractCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(WearCommand.class);

    @Autowired
    public WearCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);

        addSyntax(TokenType.ITEM_HELD);
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Output output) {
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

        if (!target.getLocation().getWorn().isEmpty()) {
            LOGGER.error("Item in inventory is also being worn! instance:{} proto:{}", target.getTemplate().getId(), target.getId());
            output.append("[default]You are already wearing %s[default].", target.getItem().getShortDescription());
            return question;
        }

        // find all items already worn
        List<MudItem> wornItems = getRepositoryBundle().getItemRepository().findByLocationHeld(ch)
            .stream()
            .filter(item -> !item.getLocation().getWorn().isEmpty())
            .toList();

        // figure out which slots are still available based on what the character is already wearing
        EnumSet<WearSlot> availableSlots = EnumSet.copyOf(ch.getCharacter().getWearSlots());
        wornItems.forEach(item -> availableSlots.removeAll(item.getLocation().getWorn()));

        EnumSet<WearSlot> occupiedSlots;

        if (WearMode.ALL.equals(target.getItem().getWearMode())) {
            // if the character doesn't have ALL the slots on the item, she can't wear it
            if (!availableSlots.containsAll(target.getItem().getWearSlots())) {
                output.append("[default]You need to take something else off before you can wear that.");
                return question;
            }

            occupiedSlots = EnumSet.copyOf(target.getItem().getWearSlots());
        } else if (WearMode.SINGLE.equals(target.getItem().getWearMode())) {
            // if the character doesn't have ANY ONE OF the slots on the item, she can't wear it
            Optional<WearSlot> shared = target.getItem().getWearSlots()
                .stream()
                .filter(availableSlots::contains)
                .findAny();

            if (shared.isEmpty()) {
                output.append("[default]You need to take something else off before you can wear that.");
                return question;
            }

            occupiedSlots = EnumSet.of(shared.get());
        } else {
            LOGGER.error("Unknown wear mode: {}", target.getItem().getWearMode());
            output.append("[red]An error has occurred. It has been reported as a bug.");
            return question;
        }

        // wear the item
        target.getLocation().setWorn(occupiedSlots);
        target.getLocation().setHeld(ch);
        target.getLocation().setRoom(null);
        getRepositoryBundle().getItemRepository().save(target);

        // make a fancy list of wear locations where the item was worn
        List<String> slotNames = occupiedSlots.stream().map(WearSlot::getName).collect(Collectors.toList());
        String slotNamesString;

        if (slotNames.size() == 1) {
            slotNamesString = slotNames.get(0);
        } else {
            StringBuilder buf = new StringBuilder(String.join(", ", slotNames));

            buf.insert(buf.lastIndexOf(",") + 1, " and");
            slotNamesString = buf.toString();
        }

        // send output to observers
        output.append("[default]You wear %s[default] on your %s.", target.getItem().getShortDescription(), slotNamesString);
        getCommService().sendToRoom(ch.getLocation().getRoom().getId(),
            new Output("[default]%s wears %s[default] on %s %s.",
                ch.getCharacter().getName(),
                target.getItem().getShortDescription(),
                ch.getCharacter().getPronoun().getPossessive(),
                slotNamesString
            ), ch);

        return question;
    }

    @Override
    public Question executeBinding(Question question, WebSocketContext webSocketContext, List<Binding> bindings, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);
        MudItem target = bindings.get(1).asItem();

        if (target.getItem().getWearSlots().isEmpty()) {
            output.append("[default]You can't wear that.");
            return question;
        }

        if (!target.getLocation().getWorn().isEmpty()) {
            LOGGER.error("Item in inventory is also being worn! instance:{} proto:{}", target.getTemplate().getId(), target.getId());
            output.append("[default]You are already wearing %s[default].", target.getItem().getShortDescription());
            return question;
        }

        // find all items already worn
        List<MudItem> wornItems = getRepositoryBundle().getItemRepository().findByLocationHeld(ch)
            .stream()
            .filter(item -> !item.getLocation().getWorn().isEmpty())
            .toList();

        // figure out which slots are still available based on what the character is already wearing
        EnumSet<WearSlot> availableSlots = EnumSet.copyOf(ch.getCharacter().getWearSlots());
        wornItems.forEach(item -> availableSlots.removeAll(item.getLocation().getWorn()));

        EnumSet<WearSlot> occupiedSlots;

        if (WearMode.ALL.equals(target.getItem().getWearMode())) {
            // if the character doesn't have ALL the slots on the item, she can't wear it
            if (!availableSlots.containsAll(target.getItem().getWearSlots())) {
                output.append("[default]You need to take something else off before you can wear that.");
                return question;
            }

            occupiedSlots = EnumSet.copyOf(target.getItem().getWearSlots());
        } else if (WearMode.SINGLE.equals(target.getItem().getWearMode())) {
            // if the character doesn't have ANY ONE OF the slots on the item, she can't wear it
            Optional<WearSlot> shared = target.getItem().getWearSlots()
                .stream()
                .filter(availableSlots::contains)
                .findAny();

            if (shared.isEmpty()) {
                output.append("[default]You need to take something else off before you can wear that.");
                return question;
            }

            occupiedSlots = EnumSet.of(shared.get());
        } else {
            LOGGER.error("Unknown wear mode: {}", target.getItem().getWearMode());
            output.append("[red]An error has occurred. It has been reported as a bug.");
            return question;
        }

        // wear the item
        target.getLocation().setWorn(occupiedSlots);
        target.getLocation().setHeld(ch);
        target.getLocation().setRoom(null);
        getRepositoryBundle().getItemRepository().save(target);

        // make a fancy list of wear locations where the item was worn
        List<String> slotNames = occupiedSlots.stream().map(WearSlot::getName).collect(Collectors.toList());
        String slotNamesString;

        if (slotNames.size() == 1) {
            slotNamesString = slotNames.get(0);
        } else {
            StringBuilder buf = new StringBuilder(String.join(", ", slotNames));

            buf.insert(buf.lastIndexOf(",") + 1, " and");
            slotNamesString = buf.toString();
        }

        // send output to observers
        output.append("[default]You wear %s[default] on your %s.", target.getItem().getShortDescription(), slotNamesString);
        getCommService().sendToRoom(ch.getLocation().getRoom().getId(),
            new Output("[default]%s wears %s[default] on %s %s.",
                ch.getCharacter().getName(),
                target.getItem().getShortDescription(),
                ch.getCharacter().getPronoun().getPossessive(),
                slotNamesString
            ), ch);

        return question;
    }
}
