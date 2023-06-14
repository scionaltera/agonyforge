package com.agonyforge.mud.demo.cli.question;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Optional;
import java.util.UUID;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;

public abstract class BaseQuestion extends com.agonyforge.mud.core.cli.AbstractQuestion {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseQuestion.class);

    private final ApplicationContext applicationContext;
    private final RepositoryBundle repositoryBundle;

    public BaseQuestion(ApplicationContext applicationContext,
                        RepositoryBundle repositoryBundle) {
        super();
        this.applicationContext = applicationContext;
        this.repositoryBundle = repositoryBundle;
    }

    protected Question getQuestion(String name) {
        return applicationContext.getBean(name, Question.class);
    }

    protected RepositoryBundle getRepositoryBundle() {
        return repositoryBundle;
    }

    protected Optional<MudCharacter> getCharacter(WebSocketContext wsContext, Output output) {
        UUID chId = (UUID) wsContext.getAttributes().get(MUD_CHARACTER);
        Optional<MudCharacter> chOptional = getRepositoryBundle().getCharacterRepository().getById(chId, true);

        if (chOptional.isEmpty()) {
            LOGGER.error("Cannot look up character by ID: {}", chId);
            output.append("[red]Unable to find your character! The error has been reported.");
            return Optional.empty();
        }

        return chOptional;
    }
}
