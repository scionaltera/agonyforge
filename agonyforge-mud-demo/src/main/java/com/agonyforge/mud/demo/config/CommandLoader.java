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

            CommandReference north = new CommandReference();
            CommandReference east = new CommandReference();
            CommandReference south = new CommandReference();
            CommandReference west = new CommandReference();
            CommandReference up = new CommandReference();
            CommandReference down = new CommandReference();

            CommandReference gossip = new CommandReference();
            CommandReference say = new CommandReference();
            CommandReference shout = new CommandReference();
            CommandReference tell = new CommandReference();
            CommandReference whisper = new CommandReference();

            CommandReference look = new CommandReference();
            CommandReference who = new CommandReference();

            CommandReference inventory = new CommandReference();
            CommandReference get = new CommandReference();
            CommandReference drop = new CommandReference();

            north.setPriority(1);
            north.setName("NORTH");
            north.setBeanName("northCommand");
            refs.add(north);

            east.setPriority(1);
            east.setName("EAST");
            east.setBeanName("eastCommand");
            refs.add(east);

            south.setPriority(1);
            south.setName("SOUTH");
            south.setBeanName("southCommand");
            refs.add(south);

            west.setPriority(1);
            west.setName("WEST");
            west.setBeanName("westCommand");
            refs.add(west);

            up.setPriority(1);
            up.setName("UP");
            up.setBeanName("upCommand");
            refs.add(up);

            down.setPriority(1);
            down.setName("DOWN");
            down.setBeanName("downCommand");
            refs.add(down);

            gossip.setPriority(10);
            gossip.setName("GOSSIP");
            gossip.setBeanName("gossipCommand");
            refs.add(gossip);

            say.setPriority(10);
            say.setName("SAY");
            say.setBeanName("sayCommand");
            refs.add(say);

            shout.setPriority(10);
            shout.setName("SHOUT");
            shout.setBeanName("shoutCommand");
            refs.add(shout);

            tell.setPriority(10);
            tell.setName("TELL");
            tell.setBeanName("tellCommand");
            refs.add(tell);

            whisper.setPriority(10);
            whisper.setName("WHISPER");
            whisper.setBeanName("whisperCommand");
            refs.add(whisper);

            look.setPriority(10);
            look.setName("LOOK");
            look.setBeanName("lookCommand");
            refs.add(look);

            who.setPriority(10);
            who.setName("WHO");
            who.setBeanName("whoCommand");
            refs.add(who);

            inventory.setPriority(10);
            inventory.setName("INVENTORY");
            inventory.setBeanName("inventoryCommand");
            refs.add(inventory);

            get.setPriority(10);
            get.setName("GET");
            get.setBeanName("getCommand");
            refs.add(get);

            drop.setPriority(10);
            drop.setName("DROP");
            drop.setBeanName("dropCommand");
            refs.add(drop);

            LOGGER.info("Creating command references");
            commandRepository.saveAll(refs);
        }
    }
}
