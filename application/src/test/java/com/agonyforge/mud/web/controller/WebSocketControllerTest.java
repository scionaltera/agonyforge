package com.agonyforge.mud.web.controller;

import com.agonyforge.mud.web.model.Input;
import com.agonyforge.mud.web.model.Output;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class WebSocketControllerTest {
    @Mock
    private Principal principal;

    @Mock
    private Message<byte[]> message;

    @Test
    void testSubscribe() {
        WebSocketController uut = new WebSocketController();
        Output result = uut.onSubscribe(principal, message);

        assertEquals(1, result.getOutput().size());
        assertEquals("Welcome!", result.getOutput().get(0));
    }

    @Test
    void testInput() {
        Input input = new Input("Hello!");
        WebSocketController uut = new WebSocketController();
        Output result = uut.onInput(input, message);

        assertEquals(1, result.getOutput().size());
        assertEquals("Echo: Hello!", result.getOutput().get(0));
    }
}
