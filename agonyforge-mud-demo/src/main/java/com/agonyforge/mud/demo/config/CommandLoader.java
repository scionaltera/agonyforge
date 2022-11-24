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
            CommandReference say = new CommandReference();

            say.setPriority(10);
            say.setName("SAY");
            say.setBeanName("sayCommand");

            refs.add(say);

            LOGGER.info("Creating command references");
            commandRepository.saveAll(refs);
        }
    }
}
