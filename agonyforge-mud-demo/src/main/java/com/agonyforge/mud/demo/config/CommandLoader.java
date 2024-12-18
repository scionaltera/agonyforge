package com.agonyforge.mud.demo.config;

import com.agonyforge.mud.demo.model.impl.CommandReference;
import com.agonyforge.mud.demo.model.repository.CommandRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
        if (commandRepository.findAll().isEmpty()) {
            List<CommandReference> refs = new ArrayList<>();

            refs.add(new CommandReference(1, "NORTH", "northCommand"));
            refs.add(new CommandReference(1, "EAST", "eastCommand"));
            refs.add(new CommandReference(1, "SOUTH", "southCommand"));
            refs.add(new CommandReference(1, "WEST", "westCommand"));
            refs.add(new CommandReference(1, "UP", "upCommand"));
            refs.add(new CommandReference(1, "DOWN", "downCommand"));

            refs.add(new CommandReference(2, "NORTHEAST", "northeastCommand"));
            refs.add(new CommandReference(2, "NORTHWEST", "northwestCommand"));
            refs.add(new CommandReference(2, "SOUTHEAST", "southeastCommand"));
            refs.add(new CommandReference(2, "SOUTHWEST", "southwestCommand"));
            refs.add(new CommandReference(2, "NE", "northeastCommand"));
            refs.add(new CommandReference(2, "NW", "northwestCommand"));
            refs.add(new CommandReference(2, "SE", "southeastCommand"));
            refs.add(new CommandReference(2, "SW", "southwestCommand"));

            refs.add(new CommandReference(5, "LOOK", "lookCommand"));
            refs.add(new CommandReference(5, "WHO", "whoCommand"));
            refs.add(new CommandReference(5, "SCORE", "scoreCommand"));
            refs.add(new CommandReference(5, "EQUIPMENT", "equipmentCommand"));
            refs.add(new CommandReference(5, "INVENTORY", "inventoryCommand"));

            refs.add(new CommandReference(10, "DROP", "dropCommand"));
            refs.add(new CommandReference(10, "GET", "getCommand"));
            refs.add(new CommandReference(10, "GIVE", "giveCommand"));
            refs.add(new CommandReference(10, "REMOVE", "removeCommand"));
            refs.add(new CommandReference(10, "WEAR", "wearCommand"));
            refs.add(new CommandReference(10, "GOSSIP", "gossipCommand"));
            refs.add(new CommandReference(10, "SAY", "sayCommand"));
            refs.add(new CommandReference(10, "SHOUT", "shoutCommand"));
            refs.add(new CommandReference(10, "TELL", "tellCommand"));
            refs.add(new CommandReference(10, "WHISPER", "whisperCommand"));

            refs.add(new CommandReference(15, "ROLL", "rollCommand"));
            refs.add(new CommandReference(15, "TIME", "timeCommand"));

            refs.add(new CommandReference(20, "REDIT", "roomEditorCommand"));

            LOGGER.info("Creating command references");
            commandRepository.saveAll(refs);
        }
    }
}
