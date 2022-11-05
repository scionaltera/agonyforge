package com.agonyforge.mud.web.controller;

import com.agonyforge.mud.web.model.Input;
import com.agonyforge.mud.web.model.Output;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import static com.agonyforge.mud.config.RemoteIpHandshakeInterceptor.SESSION_REMOTE_IP_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.SESSION_ATTRIBUTES;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.SESSION_ID_HEADER;

@ExtendWith(MockitoExtension.class)
public class WebSocketControllerTest {
    @Mock
    private Principal principal;

    private Map<String, Object> headers;

    @BeforeEach
    void setUp() {
        Map<String, Object> attributes = new HashMap<>();

        headers = new HashMap<>();

        headers.put(SESSION_ATTRIBUTES, attributes);
    }

    @Test
    void testSubscribe() {
        WebSocketController uut = new WebSocketController();
        Output result = uut.onSubscribe(principal, headers);

        assertEquals(1, result.getOutput().size());
        assertEquals("Welcome!", result.getOutput().get(0));
    }

    @Test
    void testInput() {
        Input input = new Input("Hello!");
        WebSocketController uut = new WebSocketController();
        Output result = uut.onInput(input, headers);

        assertEquals(1, result.getOutput().size());
        assertEquals("Echo: Hello!", result.getOutput().get(0));
    }
}
