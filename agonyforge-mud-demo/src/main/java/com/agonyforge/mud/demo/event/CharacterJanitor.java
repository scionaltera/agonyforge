package com.agonyforge.mud.demo.event;

import com.agonyforge.mud.core.service.SessionAttributeService;
import com.agonyforge.mud.core.service.timer.TimerEvent;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import com.agonyforge.mud.demo.service.CommService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;

@SuppressWarnings("LoggingSimilarMessage")
@Component
public class CharacterJanitor implements ApplicationListener<SessionDisconnectEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterJanitor.class);

    private final SessionAttributeService sessionAttributeService;
    private final MudCharacterRepository characterRepository;
    private final CommService commService;

    @Autowired
    public CharacterJanitor(SessionAttributeService sessionAttributeService,
                            MudCharacterRepository characterRepository,
                            CommService commService) {
        this.sessionAttributeService = sessionAttributeService;
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

                instance.setLocation(null);
                characterRepository.save(instance);

                LOGGER.info("{} has left the game", instance.getCharacter().getName());

                WebSocketContext webSocketContext = WebSocketContext.build(event.getMessage().getHeaders());
                commService.sendToAll(webSocketContext,
                    new Output("[yellow]%s has left the game!", instance.getCharacter().getName()), instance);
            }
        }
    }

    @EventListener
    public void onTimerEvent(TimerEvent event) {
        if (!TimeUnit.MINUTES.equals(event.getFrequency())) {
            return;
        }

        List<MudCharacter> disconnected = characterRepository.findAll()
            .stream()
            .filter(ch -> ch.getLocation() != null)
            .filter(ch -> sessionAttributeService.getSessionAttributes(ch.getPlayer().getWebSocketSession()).isEmpty())
            .toList();

        disconnected.forEach(ch -> {
            ch.setLocation(null);
            characterRepository.save(ch);

            LOGGER.info("{} has left the game", ch.getCharacter().getName());
            commService.sendToAll(new Output("[yellow]%s has left the game!", ch.getCharacter().getName()), ch);
        });
    }
}
