package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.Binding;
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

import static com.agonyforge.mud.demo.cli.TokenType.*;
import static com.agonyforge.mud.demo.cli.question.ingame.olc.item.ItemEditorQuestion.IEDIT_MODEL;

@Component
public class ItemEditorCommand extends AbstractCommand {
    @Autowired
    public ItemEditorCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);

        addSyntax(ITEM_ID);     // edit item ID
        addSyntax(NUMBER);      // create new item with ID
        addSyntax(ITEM_HELD);   // edit held item
        addSyntax(ITEM_GROUND); // edit item on ground
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<Binding> bindings, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);
        MudItemTemplate itemTemplate;
        MudItem item ;

        if (ITEM_ID == bindings.get(1).getType()) {
            itemTemplate = bindings.get(1).asItemTemplate();
        } else if (NUMBER == bindings.get(1).getType()) {
            try {
                Long id = bindings.get(1).asNumber();
                itemTemplate = new MudItemTemplate();

                itemTemplate.setId(id);
                itemTemplate.setItem(new ItemComponent());

                itemTemplate = getRepositoryBundle().getItemPrototypeRepository().save(itemTemplate);
            } catch (NumberFormatException e) {
                output.append("[red]Unable to create requested item prototype.");
                return question;
            }
        } else if (ITEM_GROUND == bindings.get(1).getType() || ITEM_HELD == bindings.get(1).getType()) {
            item = bindings.get(1).asItem();
            itemTemplate = item.getTemplate();
        } else {
            output.append("[red]Invalid binding type.");
            return question;
        }

        webSocketContext.getAttributes().put(IEDIT_MODEL, itemTemplate.getId());
        getCommService().sendToRoom(ch.getLocation().getRoom().getId(), new Output(
            "[yellow]%s begins editing.", ch.getCharacter().getName()), ch);

        return getApplicationContext().getBean("itemEditorQuestion", Question.class);
    }
}
