package com.agonyforge.mud.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

import static org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor.HTTP_SESSION_ID_ATTR_NAME;

public class SessionIdHandshakeHandler extends DefaultHandshakeHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionIdCopyingHandshakeInterceptor.class);
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String sessionId = (String)attributes.get(HTTP_SESSION_ID_ATTR_NAME);

        LOGGER.debug("Assigning session ID as STOMP Principal: {}", sessionId);

        return new StompPrincipal(sessionId);
    }
}
