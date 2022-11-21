package com.agonyforge.mud.core.web.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.SESSION_ATTRIBUTES;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.SESSION_ID_HEADER;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.USER_HEADER;

@ExtendWith(MockitoExtension.class)
public class WebSocketContextTest {
    @Test
    void testLegalConstructors() {
        WebSocketContext ctx1 = WebSocketContext.build(Map.of(
            USER_HEADER, mock(Principal.class),
            SESSION_ID_HEADER, "sessionId",
            SESSION_ATTRIBUTES, Map.of()
        ));

        WebSocketContext ctx2 = WebSocketContext.build(
            mock(Principal.class),
            "sessionId",
            Map.of()
        );
    }

    @ParameterizedTest
    @MethodSource
    void testIllegalHeaderConstructors(Map<String, Object> attributes) {
        assertThrows(IllegalStateException.class, () -> WebSocketContext.build(attributes));
    }

    static Stream<Map<String, Object>> testIllegalHeaderConstructors() {
        return Stream.of(
            Map.of(),
            Map.of(USER_HEADER, mock(Principal.class)),
            Map.of(SESSION_ID_HEADER, "sessionId"),
            Map.of(SESSION_ATTRIBUTES, Map.of()),
            Map.of(
                USER_HEADER, mock(Principal.class),
                SESSION_ID_HEADER, "sessionId"
            ),
            Map.of(
                SESSION_ID_HEADER, "sessionId",
                SESSION_ATTRIBUTES, Map.of()
            ),
            Map.of(
                USER_HEADER, mock(Principal.class),
                SESSION_ATTRIBUTES, Map.of()
            )
        );
    }

    @ParameterizedTest
    @MethodSource
    void testIllegalParameterConstructors(Principal principal, String sessionId, Map<String, Object> attributes) {
        assertThrows(IllegalStateException.class, () -> WebSocketContext.build(principal, sessionId, attributes));
    }

    static Stream<Arguments> testIllegalParameterConstructors() {
        return Stream.of(
            Arguments.of(null, null, null),
            Arguments.of(mock(Principal.class), null, null),
            Arguments.of(null, "sessionId", null),
            Arguments.of(null, null, Map.of()),
            Arguments.of(mock(Principal.class), "sessionId", null),
            Arguments.of(null, "sessionId", Map.of()),
            Arguments.of(mock(Principal.class), null, Map.of())
        );
    }
}
