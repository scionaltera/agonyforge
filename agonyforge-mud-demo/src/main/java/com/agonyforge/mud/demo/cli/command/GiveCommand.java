package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.Binding;
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

import static com.agonyforge.mud.demo.cli.TokenType.CHARACTER_IN_ROOM;
import static com.agonyforge.mud.demo.cli.TokenType.ITEM_HELD;

@Component
public class GiveCommand extends AbstractCommand {
    @Autowired
    public GiveCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);

        addSyntax(ITEM_HELD, CHARACTER_IN_ROOM);
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<Binding> bindings, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);
        MudItem item = bindings.get(1).asItem();
        MudCharacter target = bindings.get(2).asCharacter();

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
