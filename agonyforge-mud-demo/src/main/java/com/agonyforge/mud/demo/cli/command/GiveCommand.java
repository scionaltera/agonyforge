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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static com.agonyforge.mud.demo.cli.command.TokenType.CHARACTER_IN_ROOM;
import static com.agonyforge.mud.demo.cli.command.TokenType.ITEM_HELD;

@Component
public class GiveCommand extends AbstractCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(GiveCommand.class);

    @Autowired
    public GiveCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);

        addSyntax(ITEM_HELD, CHARACTER_IN_ROOM);
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);

        if (tokens.size() == 1) {
            output.append("[default]Which item do you want to give?");
            return question;
        }

        if (tokens.size() == 2) {
            output.append("[default]Who do you want to give it to?");
            return question;
        }

        Optional<MudItem> itemOptional = findInventoryItem(ch, tokens.get(1));
        Optional<MudCharacter> targetOptional = findRoomCharacter(ch, tokens.get(2));

        if (itemOptional.isEmpty()) {
            output.append("[default]You don't have anything like that.");
            return question;
        }

        if (targetOptional.isEmpty()) {
            output.append("[default]You don't see anyone by that name here.");
            return question;
        }

        MudItem item = itemOptional.get();
        MudCharacter target = targetOptional.get();

        if (!item.getLocation().getWorn().isEmpty()) {
            LOGGER.error("Worn item was found in inventory! id:{} proto:{}", item.getTemplate().getId(), item.getId());
            output.append("[default]You need to remove it first.");
            return question;
        }

        item.getLocation().setWorn(EnumSet.noneOf(WearSlot.class));
        item.getLocation().setHeld(target);
        item.getLocation().setRoom(null);
        getRepositoryBundle().getItemRepository().save(item);

        output.append("[default]You give %s[default] to %s.", item.getItem().getShortDescription(), target.getCharacter().getName());
        getCommService().sendTo(target, new Output("[default]%s gives %s[default] to you.", ch.getCharacter().getName(), item.getItem().getShortDescription()));
        getCommService().sendToRoom(
            ch.getLocation().getRoom().getId(),
            new Output("[default]%s gives %s[default] to %s.", ch.getCharacter().getName(), item.getItem().getShortDescription(), target.getCharacter().getName()),
            ch, target);

        return question;
    }
}
