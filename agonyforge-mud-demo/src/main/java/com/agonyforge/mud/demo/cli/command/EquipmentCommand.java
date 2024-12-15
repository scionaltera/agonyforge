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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class EquipmentCommand extends AbstractCommand {
    @Autowired
    public EquipmentCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);
        Map<WearSlot, MudItem> inventory = getRepositoryBundle().getItemRepository().getByChId(ch.getId())
                .stream()
                .filter(item -> item.getWorn() != null)
                .collect(Collectors.toMap(MudItem::getWorn, Function.identity()));

        output.append("[default]You are using:");

        if (inventory.isEmpty()) {
            output.append("Nothing.");
        } else {
            inventory.entrySet()
                .stream()
                .sorted()
                .forEach(entry -> output.append("[default]&lt;%s>\t%s",
                    entry.getKey().getPhrase(), entry.getValue().getShortDescription()));
        }

        return question;
    }
}
