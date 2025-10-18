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
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EquipmentCommand extends AbstractCommand {
    @Autowired
    public EquipmentCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);

        addSyntax();
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);
        Map<WearSlot, MudItem> inventory = getRepositoryBundle().getItemRepository().findByLocationHeld(ch)
                .stream()
                .filter(item -> item.getLocation() != null && !item.getLocation().getWorn().isEmpty())
                .flatMap(item -> item.getLocation().getWorn().stream().map(slot -> new ImmutablePair<>(slot, item)))
                .collect(Collectors.toMap(ImmutablePair::getLeft, ImmutablePair::getRight));

        output.append("[default]You are using:");

        if (inventory.isEmpty()) {
            output.append("Nothing.");
        } else {
            inventory.entrySet()
                .stream()
                .sorted(Comparator.comparingInt(entry -> entry.getKey().ordinal()))
                .forEach(entry -> output.append("[default]&lt;%s&gt;\t%s",
                    entry.getKey().getPhrase(), entry.getValue().getItem().getShortDescription()));
        }

        return question;
    }

    @Override
    public Question executeBinding(Question question, WebSocketContext webSocketContext, List<Binding> bindings, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);
        Map<WearSlot, MudItem> inventory = getRepositoryBundle().getItemRepository().findByLocationHeld(ch)
            .stream()
            .filter(item -> item.getLocation() != null && !item.getLocation().getWorn().isEmpty())
            .flatMap(item -> item.getLocation().getWorn().stream().map(slot -> new ImmutablePair<>(slot, item)))
            .collect(Collectors.toMap(ImmutablePair::getLeft, ImmutablePair::getRight));

        output.append("[default]You are using:");

        if (inventory.isEmpty()) {
            output.append("Nothing.");
        } else {
            inventory.entrySet()
                .stream()
                .sorted(Comparator.comparingInt(entry -> entry.getKey().ordinal()))
                .forEach(entry -> output.append("[default]&lt;%s&gt;\t%s",
                    entry.getKey().getPhrase(), entry.getValue().getItem().getShortDescription()));
        }

        return question;
    }
}
