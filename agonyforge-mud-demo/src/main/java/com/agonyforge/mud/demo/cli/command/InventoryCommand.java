package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudItem;
import com.agonyforge.mud.demo.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InventoryCommand extends AbstractCommand {
    @Autowired
    public InventoryCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);

        addSyntax();
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<Binding> bindings, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);
        List<MudItem> items = getRepositoryBundle().getItemRepository().findByLocationHeld(ch);

        output.append("[default]You are carrying:");

        List<MudItem> held = items
            .stream()
            .filter(item -> item.getLocation().getWorn().isEmpty()).toList();

        if (held.isEmpty()) {
            output.append("[default]Nothing.");
        } else {
            held.forEach(item -> output.append(String.format("(%s) %s", item.getTemplate().getId(), item.getItem().getShortDescription())));
        }

        return question;
    }
}
