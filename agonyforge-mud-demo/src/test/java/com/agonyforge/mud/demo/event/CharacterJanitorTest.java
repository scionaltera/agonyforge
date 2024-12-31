package com.agonyforge.mud.demo.event;

import com.agonyforge.mud.core.service.SessionAttributeService;
import com.agonyforge.mud.core.service.timer.TimerEvent;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.PlayerComponent;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import com.agonyforge.mud.demo.service.CommService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CharacterJanitorTest {
    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private SessionAttributeService sessionAttributeService;

    @Mock
    private CommService commService;

    @Mock
    private SessionDisconnectEvent event;

    @Mock
    private Message<byte[]> message;

    @Mock
    private SimpMessageHeaderAccessor simpMessageHeaderAccessor;

    @Mock
    private PlayerComponent chPlayer;

    @Mock
    private PlayerComponent otherPlayer;

    @Mock
    private MudCharacter ch;

    @Mock
    private MudCharacter other;

    @Mock
    private TimerEvent timerEvent;

    private final Random random = new Random();

    @Test
    void testOnApplicationEvent() {
        Long chId = random.nextLong();
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_CHARACTER, chId);

        try (MockedStatic<SimpMessageHeaderAccessor> headerAccessor = mockStatic(SimpMessageHeaderAccessor.class)) {
            headerAccessor.when(() -> SimpMessageHeaderAccessor.wrap(any())).thenReturn(simpMessageHeaderAccessor);
            headerAccessor.when(() -> SimpMessageHeaderAccessor.getUser(any())).thenReturn(mock(Principal.class));
            headerAccessor.when(() -> SimpMessageHeaderAccessor.getSessionId(any())).thenReturn(UUID.randomUUID().toString());
            headerAccessor.when(() -> SimpMessageHeaderAccessor.getSessionAttributes(any())).thenReturn(attributes);

            when(event.getMessage()).thenReturn(message);
            when(simpMessageHeaderAccessor.getSessionAttributes()).thenReturn(attributes);
            when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));

            CharacterJanitor uut = new CharacterJanitor(sessionAttributeService, characterRepository, commService);

            uut.onApplicationEvent(event);

            verify(characterRepository).delete(ch);
            verify(commService).sendToAll(any(WebSocketContext.class), any(Output.class), eq(ch));
        }
    }

    @Test
    void testOnTimerEvent() {
        CharacterJanitor uut = new CharacterJanitor(sessionAttributeService, characterRepository, commService);

        when(timerEvent.getFrequency()).thenReturn(TimeUnit.MINUTES);
        when(ch.getPlayer()).thenReturn(chPlayer);
        when(chPlayer.getWebSocketSession()).thenReturn("abc");
        when(other.getPlayer()).thenReturn(otherPlayer);
        when(otherPlayer.getWebSocketSession()).thenReturn("def");
        when(characterRepository.findAll()).thenReturn(List.of(ch, other));

        uut.onTimerEvent(timerEvent);

        verify(sessionAttributeService, times(2)).getSessionAttributes(anyString());
        verify(characterRepository, times(2)).delete(any(MudCharacter.class));
        verify(commService, times(2)).sendToAll(any(Output.class), any(MudCharacter.class));
    }
}
