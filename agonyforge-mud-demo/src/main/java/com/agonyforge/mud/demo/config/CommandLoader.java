package com.agonyforge.mud.demo.config;

import com.agonyforge.mud.models.dynamodb.impl.CommandReference;
import com.agonyforge.mud.models.dynamodb.repository.CommandRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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

            north.setPriority(1);
            north.setName("NORTH");
            north.setBeanName("northCommand");

            east.setPriority(1);
            east.setName("EAST");
            east.setBeanName("eastCommand");

            south.setPriority(1);
            south.setName("SOUTH");
            south.setBeanName("southCommand");

            west.setPriority(1);
            west.setName("WEST");
            west.setBeanName("westCommand");

            up.setPriority(1);
            up.setName("UP");
            up.setBeanName("upCommand");

            down.setPriority(1);
            down.setName("DOWN");
            down.setBeanName("downCommand");

            gossip.setPriority(10);
            gossip.setName("GOSSIP");
            gossip.setBeanName("gossipCommand");

            say.setPriority(10);
            say.setName("SAY");
            say.setBeanName("sayCommand");

            shout.setPriority(10);
            shout.setName("SHOUT");
            shout.setBeanName("shoutCommand");

            tell.setPriority(10);
            tell.setName("TELL");
            tell.setBeanName("tellCommand");

            whisper.setPriority(10);
            whisper.setName("WHISPER");
            whisper.setBeanName("whisperCommand");

            look.setPriority(10);
            look.setName("LOOK");
            look.setBeanName("lookCommand");

            who.setPriority(10);
            who.setName("WHO");
            who.setBeanName("whoCommand");

            LOGGER.info("Creating command references");
            commandRepository.saveAll(List.of(gossip, say, shout, look, tell, whisper, north, east, south, west, up,
                down, who));
        }
    }
}
