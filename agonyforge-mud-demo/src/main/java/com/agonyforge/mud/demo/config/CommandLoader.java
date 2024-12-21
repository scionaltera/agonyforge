package com.agonyforge.mud.demo.config;

import com.agonyforge.mud.demo.model.impl.CommandReference;
import com.agonyforge.mud.demo.model.impl.Role;
import com.agonyforge.mud.demo.model.repository.CommandRepository;

import com.agonyforge.mud.demo.model.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CommandLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandLoader.class);

    private final CommandRepository commandRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public CommandLoader(CommandRepository commandRepository, RoleRepository roleRepository) {
        this.commandRepository = commandRepository;
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void loadCommands() {
        if (commandRepository.findAll().isEmpty()) {
            Map<String, CommandReference> refs = new HashMap<>();

            refs.put("NORTH", new CommandReference(1, "NORTH", "northCommand"));
            refs.put("EAST", new CommandReference(1, "EAST", "eastCommand"));
            refs.put("SOUTH", new CommandReference(1, "SOUTH", "southCommand"));
            refs.put("WEST", new CommandReference(1, "WEST", "westCommand"));
            refs.put("UP", new CommandReference(1, "UP", "upCommand"));
            refs.put("DOWN", new CommandReference(1, "DOWN", "downCommand"));

            refs.put("NORTHEAST", new CommandReference(2, "NORTHEAST", "northeastCommand"));
            refs.put("NORTHWEST", new CommandReference(2, "NORTHWEST", "northwestCommand"));
            refs.put("SOUTHEAST", new CommandReference(2, "SOUTHEAST", "southeastCommand"));
            refs.put("SOUTHWEST", new CommandReference(2, "SOUTHWEST", "southwestCommand"));
            refs.put("NE", new CommandReference(2, "NE", "northeastCommand"));
            refs.put("NW", new CommandReference(2, "NW", "northwestCommand"));
            refs.put("SE", new CommandReference(2, "SE", "southeastCommand"));
            refs.put("SW", new CommandReference(2, "SW", "southwestCommand"));

            refs.put("LOOK", new CommandReference(5, "LOOK", "lookCommand"));
            refs.put("WHO", new CommandReference(5, "WHO", "whoCommand"));
            refs.put("SCORE", new CommandReference(5, "SCORE", "scoreCommand"));
            refs.put("EQUIPMENT", new CommandReference(5, "EQUIPMENT", "equipmentCommand"));
            refs.put("INVENTORY", new CommandReference(5, "INVENTORY", "inventoryCommand"));

            refs.put("DROP", new CommandReference(10, "DROP", "dropCommand"));
            refs.put("GET", new CommandReference(10, "GET", "getCommand"));
            refs.put("GIVE", new CommandReference(10, "GIVE", "giveCommand"));
            refs.put("REMOVE", new CommandReference(10, "REMOVE", "removeCommand"));
            refs.put("WEAR", new CommandReference(10, "WEAR", "wearCommand"));
            refs.put("GOSSIP", new CommandReference(10, "GOSSIP", "gossipCommand"));
            refs.put("SAY", new CommandReference(10, "SAY", "sayCommand"));
            refs.put("SHOUT", new CommandReference(10, "SHOUT", "shoutCommand"));
            refs.put("TELL", new CommandReference(10, "TELL", "tellCommand"));
            refs.put("WHISPER", new CommandReference(10, "WHISPER", "whisperCommand"));

            refs.put("ROLL", new CommandReference(15, "ROLL", "rollCommand"));
            refs.put("TIME", new CommandReference(15, "TIME", "timeCommand"));

            refs.put("REDIT", new CommandReference(20, "REDIT", "roomEditorCommand"));
            refs.put("IEDIT", new CommandReference(20, "IEDIT", "itemEditorCommand"));

            refs.put("CREATE", new CommandReference(30, "CREATE", "createCommand"));
            refs.put("PURGE", new CommandReference(30, "PURGE", "purgeCommand"));

            LOGGER.info("Creating command references");
            commandRepository.saveAll(refs.values());

            Role player = new Role();

            player.setName("Player");
            player.getCommands().add(refs.get("NORTH"));
            player.getCommands().add(refs.get("EAST"));
            player.getCommands().add(refs.get("SOUTH"));
            player.getCommands().add(refs.get("WEST"));
            player.getCommands().add(refs.get("UP"));
            player.getCommands().add(refs.get("DOWN"));

            player.getCommands().add(refs.get("NORTHEAST"));
            player.getCommands().add(refs.get("NORTHWEST"));
            player.getCommands().add(refs.get("SOUTHEAST"));
            player.getCommands().add(refs.get("SOUTHWEST"));
            player.getCommands().add(refs.get("NE"));
            player.getCommands().add(refs.get("NW"));
            player.getCommands().add(refs.get("SE"));
            player.getCommands().add(refs.get("SW"));

            player.getCommands().add(refs.get("LOOK"));
            player.getCommands().add(refs.get("WHO"));
            player.getCommands().add(refs.get("SCORE"));

            player.getCommands().add(refs.get("EQUIPMENT"));
            player.getCommands().add(refs.get("INVENTORY"));
            player.getCommands().add(refs.get("DROP"));
            player.getCommands().add(refs.get("GET"));
            player.getCommands().add(refs.get("GIVE"));
            player.getCommands().add(refs.get("REMOVE"));
            player.getCommands().add(refs.get("WEAR"));

            player.getCommands().add(refs.get("GOSSIP"));
            player.getCommands().add(refs.get("SAY"));
            player.getCommands().add(refs.get("SHOUT"));
            player.getCommands().add(refs.get("TELL"));
            player.getCommands().add(refs.get("WHISPER"));

            roleRepository.save(player);
        }
    }
}
