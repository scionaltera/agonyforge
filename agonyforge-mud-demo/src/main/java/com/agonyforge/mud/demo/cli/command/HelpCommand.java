package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.CommandReference;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.repository.CommandRepository;
import com.agonyforge.mud.demo.service.CommService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class HelpCommand extends AbstractCommand {
    private final CommandRepository commandRepository;

    public HelpCommand(RepositoryBundle repositoryBundle,
                       CommService commService,
                       ApplicationContext applicationContext,
                       CommandRepository commandRepository) {
        super(repositoryBundle, commService, applicationContext);

        this.commandRepository = commandRepository;
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);
        Set<CommandReference> commands;

        if (ch.getTemplate().getId() == 1L) {
            commands = new HashSet<>(commandRepository.findAll());
        } else {
            commands = ch.getPlayer().getRoles()
                .stream()
                .flatMap(role -> role.getCommands().stream())
                .collect(Collectors.toSet());
        }

        output
            .append("[white]== Available Commands ==")
            .append("[default]Commands are listed in order of priority, from highest to lowest. Typing a partial command will run the first command from the top of the list that matches.")
            .append("[default]You can use the up and down arrows on your keyboard to move up and down in your command history.")
            .append("");

        commands
            .stream()
            .sorted(Comparator.comparingInt(CommandReference::getPriority))
            .forEachOrdered(cmd -> output.append("[yellow]%-12s[white]: [dyellow]%s",
                cmd.getName().toUpperCase(Locale.ROOT),
                cmd.getDescription()));

        return question;
    }
}
