package com.agonyforge.mud.demo.event;

import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CharacterJanitorTest {
    @Mock
    private MudCharacterRepository characterRepository;

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

            when(event.getMessage()).thenReturn(message);
            when(simpMessageHeaderAccessor.getSessionAttributes()).thenReturn(attributes);
            when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.of(ch));

            CharacterJanitor uut = new CharacterJanitor(characterRepository);

            uut.onApplicationEvent(event);

            verify(characterRepository).delete(ch);
        }
    }
}
