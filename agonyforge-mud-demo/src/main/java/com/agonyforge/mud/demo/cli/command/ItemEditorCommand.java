package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudItem;
import com.agonyforge.mud.demo.model.impl.MudItemPrototype;
import com.agonyforge.mud.demo.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.agonyforge.mud.demo.cli.question.ingame.olc.item.ItemEditorQuestion.IEDIT_MODEL;

@Component
public class ItemEditorCommand extends AbstractCommand {
    private final ApplicationContext applicationContext;

    @Autowired
    public ItemEditorCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);

        this.applicationContext = applicationContext;
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
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

        Optional<MudItemPrototype> itemProto = findItemToEdit(ch, tokens.get(1));

        if (itemProto.isEmpty()) {
            try {
                Long id = Long.parseLong(tokens.get(1));
                MudItemPrototype itemPrototype = new MudItemPrototype();

                itemPrototype.setId(id);

                itemProto = Optional.of(getRepositoryBundle().getItemPrototypeRepository().save(itemPrototype));
            } catch (NumberFormatException e) {
                output.append("[red]Unable to find or create requested item prototype.");
                return question;
            }
        }

        webSocketContext.getAttributes().put(IEDIT_MODEL, itemProto.get().getId());
        getCommService().sendToRoom(webSocketContext, ch.getRoomId(), new Output(
            "[yellow]%s begins editing.", ch.getCharacter().getName()), ch);

        return applicationContext.getBean("itemEditorQuestion", Question.class);
    }

    private Optional<MudItemPrototype> findItemToEdit(MudCharacter ch, String token) {
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
