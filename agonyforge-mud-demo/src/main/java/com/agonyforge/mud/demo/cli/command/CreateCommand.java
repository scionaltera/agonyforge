package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.constant.WearSlot;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudItem;
import com.agonyforge.mud.demo.model.impl.MudItemTemplate;
import com.agonyforge.mud.demo.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static com.agonyforge.mud.demo.cli.TokenType.ITEM_ID;

@Component
public class CreateCommand extends AbstractCommand {
    @Autowired
    public CreateCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);

        addSyntax(ITEM_ID);
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);

        if (tokens.size() != 2) {
            output.append("[default]What is the ID of the item you'd like to create?");
            return question;
        }

        Optional<MudItemTemplate> itemProto = Optional.empty();

        try {
            Long id = Long.parseLong(tokens.get(1));
            itemProto = getRepositoryBundle().getItemPrototypeRepository().findById(id);
        } catch (NumberFormatException e) {
            // TODO search for item prototypes by name?
        }

        if (itemProto.isEmpty()) {
            output.append("[red]There is no item with that ID.");
            return question;
        }

        MudItem item = itemProto.get().buildInstance();
        item.getLocation().setWorn(EnumSet.noneOf(WearSlot.class));
        item.getLocation().setHeld(ch);
        item.getLocation().setRoom(null);
        item = getRepositoryBundle().getItemRepository().save(item);

        output.append("[yellow]You wave your hand, and %s appears!", item.getItem().getShortDescription());
        getCommService().sendToRoom(ch.getLocation().getRoom().getId(),
            new Output("[yellow]%s waves %s hand, and %s appears!", ch.getCharacter().getName(), ch.getCharacter().getPronoun().getPossessive(), item.getItem().getShortDescription()),
            ch);

        return question;
    }
}
