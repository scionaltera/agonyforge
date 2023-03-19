package com.agonyforge.mud.demo.config;

import com.agonyforge.mud.models.dynamodb.impl.CommandReference;
import com.agonyforge.mud.models.dynamodb.repository.CommandRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class CommandLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandLoader.class);

    private final CommandRepository commandRepository;

    @Autowired
    public CommandLoader(CommandRepository commandRepository) {
        this.commandRepository = commandRepository;
    }

    @PostConstruct
    public void loadCommands() {
        if (commandRepository.getByPriority().isEmpty()) {
            List<CommandReference> refs = new ArrayList<>();

            refs.add(new CommandReference("01", "NORTH", "northCommand"));
            refs.add(new CommandReference("01", "EAST", "eastCommand"));
            refs.add(new CommandReference("01", "SOUTH", "southCommand"));
            refs.add(new CommandReference("01", "WEST", "westCommand"));
            refs.add(new CommandReference("01", "UP", "upCommand"));
            refs.add(new CommandReference("01", "DOWN", "downCommand"));

            refs.add(new CommandReference("05", "LOOK", "lookCommand"));
            refs.add(new CommandReference("05", "WHO", "whoCommand"));
            refs.add(new CommandReference("05", "SCORE", "scoreCommand"));
            refs.add(new CommandReference("05", "EQUIPMENT", "equipmentCommand"));
            refs.add(new CommandReference("05", "INVENTORY", "inventoryCommand"));

            refs.add(new CommandReference("10", "DROP", "dropCommand"));
            refs.add(new CommandReference("10", "GET", "getCommand"));
            refs.add(new CommandReference("10", "GIVE", "giveCommand"));
            refs.add(new CommandReference("10", "REMOVE", "removeCommand"));
            refs.add(new CommandReference("10", "WEAR", "wearCommand"));
            refs.add(new CommandReference("10", "GOSSIP", "gossipCommand"));
            refs.add(new CommandReference("10", "SAY", "sayCommand"));
            refs.add(new CommandReference("10", "SHOUT", "shoutCommand"));
            refs.add(new CommandReference("10", "TELL", "tellCommand"));
            refs.add(new CommandReference("10", "WHISPER", "whisperCommand"));

            LOGGER.info("Creating command references");
            commandRepository.saveAll(refs);
        }
    }
}
