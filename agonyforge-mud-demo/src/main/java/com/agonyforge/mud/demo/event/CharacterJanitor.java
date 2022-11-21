package com.agonyforge.mud.demo.event;

import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;

@Component
public class CharacterJanitor implements ApplicationListener<SessionDisconnectEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterJanitor.class);

    private final MudCharacterRepository characterRepository;

    @Autowired
    public CharacterJanitor(MudCharacterRepository characterRepository) {
        this.characterRepository = characterRepository;
    }

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> attributes = headerAccessor.getSessionAttributes();

        if (attributes != null) {
            UUID chId = (UUID) attributes.get(MUD_CHARACTER);
            Optional<MudCharacter> instanceOptional = characterRepository.getById(chId, false);

            if (instanceOptional.isPresent()) {
                MudCharacter instance = instanceOptional.get();

                // TODO copy relevant differences in instance back to prototype
                characterRepository.delete(instance);

                LOGGER.info("{} has left the game.", instance.getName());
            }
        }
    }
}
