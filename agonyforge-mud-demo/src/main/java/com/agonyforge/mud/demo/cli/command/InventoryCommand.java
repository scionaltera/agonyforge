package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.impl.MudItem;
import com.agonyforge.mud.models.dynamodb.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InventoryCommand extends AbstractCommand {
    @Autowired
    public InventoryCommand(RepositoryBundle repositoryBundle, CommService commService) {
        super(repositoryBundle, commService);
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);
        List<MudItem> items = getRepositoryBundle().getItemRepository().getByCharacter(ch.getId());

        output.append("[default]You are carrying:");

        if (items.isEmpty()) {
            output.append("[default]Nothing.");
        } else {
            items
                .stream()
                .filter(item -> item.getWorn() == null)
                .forEach(item -> output.append(item.getShortDescription()));
        }

        return question;
    }
}
