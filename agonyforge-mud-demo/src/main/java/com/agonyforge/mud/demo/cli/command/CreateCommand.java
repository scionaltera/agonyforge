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

@Component
public class CreateCommand extends AbstractCommand {
    @Autowired
    public CreateCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);

        if (tokens.size() != 2) {
            output.append("[default]What is the ID of the item you'd like to create?");
            return question;
        }

        Long id = Long.parseLong(tokens.get(1));
        Optional<MudItemPrototype> itemProto = getRepositoryBundle().getItemPrototypeRepository().findById(id);

        if (itemProto.isEmpty()) {
            output.append("[red]There is no item with that ID.");
            return question;
        }

        MudItem item = itemProto.get().buildInstance();
        item.setCharacterId(ch.getId());
        item = getRepositoryBundle().getItemRepository().save(item);

        output.append("[yellow]You wave your hand, and %s appears!", item.getShortDescription());
        getCommService().sendToRoom(webSocketContext, ch.getRoomId(),
            new Output("[yellow]%s waves %s hand, and %s appears!", ch.getName(), ch.getPronoun().getPossessive(), item.getShortDescription()));

        return question;
    }
}
