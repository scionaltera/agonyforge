package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.ItemComponent;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudItem;
import com.agonyforge.mud.demo.model.impl.MudItemTemplate;
import com.agonyforge.mud.demo.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.agonyforge.mud.demo.cli.TokenType.*;
import static com.agonyforge.mud.demo.cli.question.ingame.olc.item.ItemEditorQuestion.IEDIT_MODEL;

@Component
public class ItemEditorCommand extends AbstractCommand {
    @Autowired
    public ItemEditorCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);

        addSyntax(ITEM_ID);
        addSyntax(ITEM_HELD);
        addSyntax(ITEM_GROUND);
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);

        // IEDIT <number> <-- edit a specific item by number (can be existing or new)
        // IEDIT <name>   <-- edit an item you're holding
        // IEDIT <name>   <-- edit an item in the room you're standing in

        if (tokens.size() != 2) {
            output
                .append("Expected one argument.", tokens)
                .append("[default]Valid arguments:")
                .append("IEDIT &lt;item number&gt;")
                .append("IEDIT &lt;item in inventory&gt;")
                .append("IEDIT &lt;item on ground&gt;");
            return question;
        }

        Optional<MudItemTemplate> itemProto = findItemToEdit(ch, tokens.get(1));

        if (itemProto.isEmpty()) {
            try {
                Long id = Long.parseLong(tokens.get(1));
                MudItemTemplate itemPrototype = new MudItemTemplate();

                itemPrototype.setId(id);
                itemPrototype.setItem(new ItemComponent());

                itemProto = Optional.of(getRepositoryBundle().getItemPrototypeRepository().save(itemPrototype));
            } catch (NumberFormatException e) {
                output.append("[red]Unable to find or create requested item prototype.");
                return question;
            }
        }

        webSocketContext.getAttributes().put(IEDIT_MODEL, itemProto.get().getId());
        getCommService().sendToRoom(ch.getLocation().getRoom().getId(), new Output(
            "[yellow]%s begins editing.", ch.getCharacter().getName()), ch);

        return getApplicationContext().getBean("itemEditorQuestion", Question.class);
    }

    private Optional<MudItemTemplate> findItemToEdit(MudCharacter ch, String token) {
        try {
            Long id = Long.parseLong(token);

            return getRepositoryBundle().getItemPrototypeRepository().findById(id);
        } catch (NumberFormatException e) {
            Optional<MudItem> item = findInventoryItem(ch, token);

            if (item.isEmpty()) {
                item = findRoomItem(ch, token);
            }

            return item.flatMap(mudItem -> getRepositoryBundle().getItemPrototypeRepository().findById(mudItem.getId()));
        }
    }
}
