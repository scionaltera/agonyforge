package com.agonyforge.mud.web.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@ExtendWith(MockitoExtension.class)
public class SessionDisconnectListenerTest {
    @Mock
    private SessionDisconnectEvent event;

    @Test
    void testApplicationEvent() {
        SessionDisconnectListener uut = new SessionDisconnectListener();

        uut.onApplicationEvent(event);
    }
}
