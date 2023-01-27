package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Optional;
import java.util.UUID;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;

public abstract class AbstractQuestion extends com.agonyforge.mud.core.cli.AbstractQuestion {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractQuestion.class);

    private final ApplicationContext applicationContext;
    private final MudCharacterRepository characterRepository;
    private final MudItemRepository itemRepository;

    public AbstractQuestion(ApplicationContext applicationContext,
                            MudCharacterRepository characterRepository,
                            MudItemRepository itemRepository) {
        super();
        this.applicationContext = applicationContext;
        this.characterRepository = characterRepository;
        this.itemRepository = itemRepository;
    }

    protected Question getQuestion(String name) {
        return applicationContext.getBean(name, Question.class);
    }

    protected Optional<MudCharacter> getCharacter(WebSocketContext wsContext, Output output) {
        UUID chId = (UUID) wsContext.getAttributes().get(MUD_CHARACTER);
        Optional<MudCharacter> chOptional = characterRepository.getById(chId, true);

        if (chOptional.isEmpty()) {
            LOGGER.error("Cannot look up character by ID: {}", chId);
            output.append("[red]Unable to find your character! The error has been reported.");
            return Optional.empty();
        }

        return chOptional;
    }

    protected MudCharacterRepository getCharacterRepository() {
        return characterRepository;
    }

    protected MudItemRepository getItemRepository() {
        return itemRepository;
    }
}
