package com.agonyforge.mud.demo.event;

import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CharacterJanitorTest {
    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private CommService commService;

    @Mock
    private SessionDisconnectEvent event;

    @Mock
    private Message<byte[]> message;

    @Mock
    private SimpMessageHeaderAccessor simpMessageHeaderAccessor;

    @Mock
    private MudCharacter ch;

    @Test
    void testOnApplicationEvent() {
        UUID chId = UUID.randomUUID();
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_CHARACTER, chId);

        try (MockedStatic<SimpMessageHeaderAccessor> headerAccessor = mockStatic(SimpMessageHeaderAccessor.class)) {
            headerAccessor.when(() -> SimpMessageHeaderAccessor.wrap(any())).thenReturn(simpMessageHeaderAccessor);
            headerAccessor.when(() -> SimpMessageHeaderAccessor.getUser(any())).thenReturn(mock(Principal.class));
            headerAccessor.when(() -> SimpMessageHeaderAccessor.getSessionId(any())).thenReturn(UUID.randomUUID().toString());
            headerAccessor.when(() -> SimpMessageHeaderAccessor.getSessionAttributes(any())).thenReturn(attributes);

            when(event.getMessage()).thenReturn(message);
            when(simpMessageHeaderAccessor.getSessionAttributes()).thenReturn(attributes);
            when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.of(ch));

            CharacterJanitor uut = new CharacterJanitor(characterRepository, commService);

            uut.onApplicationEvent(event);

            verify(characterRepository).delete(ch);
            verify(commService).sendToAll(any(WebSocketContext.class), any(Output.class), eq(ch));
        }
    }
}
