package com.agonyforge.mud.core.web.controller;

import com.agonyforge.mud.core.service.InMemoryUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SessionDisconnectListenerTest {
    @Mock
    private SessionDisconnectEvent event;

    @Mock
    private InMemoryUserRepository userRepository;

    @Test
    void testApplicationEvent() {
        SessionDisconnectListener uut = new SessionDisconnectListener(userRepository);

        uut.onApplicationEvent(event);

        verify(userRepository).getWsSessionNames();
    }
}
