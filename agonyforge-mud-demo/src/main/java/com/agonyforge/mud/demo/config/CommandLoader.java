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
            CommandReference gossip = new CommandReference();
            CommandReference say = new CommandReference();
            CommandReference shout = new CommandReference();
            CommandReference look = new CommandReference();
            CommandReference tell = new CommandReference();
            CommandReference whisper = new CommandReference();

            gossip.setPriority(10);
            gossip.setName("GOSSIP");
            gossip.setBeanName("gossipCommand");

            say.setPriority(10);
            say.setName("SAY");
            say.setBeanName("sayCommand");

            shout.setPriority(10);
            shout.setName("SHOUT");
            shout.setBeanName("shoutCommand");

            look.setPriority(10);
            look.setName("LOOK");
            look.setBeanName("lookCommand");

            tell.setPriority(10);
            tell.setName("TELL");
            tell.setBeanName("tellCommand");

            whisper.setPriority(10);
            whisper.setName("WHISPER");
            whisper.setBeanName("whisperCommand");

            LOGGER.info("Creating command references");
            commandRepository.saveAll(List.of(gossip, say, shout, look, tell, whisper));
        }
    }
}
