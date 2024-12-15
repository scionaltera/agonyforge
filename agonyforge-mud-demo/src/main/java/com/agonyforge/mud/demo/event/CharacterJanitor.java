package com.agonyforge.mud.demo.event;

import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import com.agonyforge.mud.demo.service.CommService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.Optional;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;

@Component
public class CharacterJanitor implements ApplicationListener<SessionDisconnectEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterJanitor.class);

    private final MudCharacterRepository characterRepository;
    private final CommService commService;

    @Autowired
    public CharacterJanitor(MudCharacterRepository characterRepository,
                            CommService commService) {
        this.characterRepository = characterRepository;
        this.commService = commService;
    }

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> attributes = headerAccessor.getSessionAttributes();

        if (attributes != null && attributes.containsKey(MUD_CHARACTER)) {
            Long chId = (Long)attributes.get(MUD_CHARACTER);
            Optional<MudCharacter> instanceOptional = characterRepository.findById(chId);

            if (instanceOptional.isPresent()) {
                MudCharacter instance = instanceOptional.get();

                // TODO copy relevant differences in instance back to prototype
                characterRepository.delete(instance);

                LOGGER.info("{} has left the game.", instance.getName());

                WebSocketContext webSocketContext = WebSocketContext.build(event.getMessage().getHeaders());
                commService.sendToAll(webSocketContext,
                    new Output("[yellow]%s has left the game!", instance.getName()), instance);
            }
        }
    }
}
