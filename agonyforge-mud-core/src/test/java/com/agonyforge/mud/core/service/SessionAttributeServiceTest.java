package com.agonyforge.mud.core.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageType;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.MESSAGE_TYPE_HEADER;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.SESSION_ATTRIBUTES;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.SESSION_ID_HEADER;

@ExtendWith(MockitoExtension.class)
public class SessionAttributeServiceTest {
    @Mock
    private Message<?> message;

    @Mock
    private MessageHeaders headers;

    @Mock
    private MessageChannel channel;

    private final Map<String, Object> attributes = new HashMap<>();

    @BeforeEach
    void setUp() {
        attributes.clear();
        attributes.put("foo", "bar");
    }

    @Test
    void preSendNone() {
        when(message.getHeaders()).thenReturn(headers);
        when(headers.get(eq(MESSAGE_TYPE_HEADER))).thenReturn(SimpMessageType.OTHER);

        SessionAttributeService uut = new SessionAttributeService();

        Message<?> m = uut.preSend(message, channel);

        Map<String, Object> result = uut.getSessionAttributes("sessionId");

        assertEquals(message, m);
        assertEquals(0, result.size());
    }

    @ParameterizedTest
    @EnumSource(value = SimpMessageType.class, names = {
        "CONNECT",
        "SUBSCRIBE",
        "MESSAGE"
    })
    void preSendPut(SimpMessageType messageType) {
        when(message.getHeaders()).thenReturn(headers);
        when(headers.get(eq(SESSION_ID_HEADER))).thenReturn("sessionId");
        when(headers.get(eq(MESSAGE_TYPE_HEADER))).thenReturn(messageType);
        when(headers.get(eq(SESSION_ATTRIBUTES))).thenReturn(attributes);

        SessionAttributeService uut = new SessionAttributeService();

        Message<?> m = uut.preSend(message, channel);

        Map<String, Object> result = uut.getSessionAttributes("sessionId");

        assertEquals(message, m);
        assertEquals(1, result.size());
        assertEquals("bar", result.get("foo"));
    }

    @ParameterizedTest
    @EnumSource(value = SimpMessageType.class, names = {
        "DISCONNECT",
        "UNSUBSCRIBE"
    })
    void preSendPutAndRemove(SimpMessageType messageType) {
        when(message.getHeaders()).thenReturn(headers);
        when(headers.get(eq(SESSION_ID_HEADER))).thenReturn("sessionId");
        when(headers.get(eq(MESSAGE_TYPE_HEADER)))
            .thenReturn(SimpMessageType.MESSAGE)
            .thenReturn(messageType);
        when(headers.get(eq(SESSION_ATTRIBUTES))).thenReturn(attributes);

        SessionAttributeService uut = new SessionAttributeService();

        Message<?> m1 = uut.preSend(message, channel); // MESSAGE - writes to map

        Map<String, Object> result1 = uut.getSessionAttributes("sessionId");

        assertEquals(message, m1);
        assertEquals(1, result1.size());
        assertEquals("bar", result1.get("foo"));

        Message<?> m2 = uut.preSend(message, channel); // parameter - removes from map

        Map<String, Object> result2 = uut.getSessionAttributes("sessionId");

        assertEquals(message, m2);
        assertEquals(0, result2.size());
    }
}
