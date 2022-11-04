package com.agonyforge.mud.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

import static org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor.HTTP_SESSION_ID_ATTR_NAME;

/*
 * Hacky workaround for https://github.com/spring-projects/spring-session/issues/561
 *
 * "SPRING.SESSION.ID" comes from
 * org.springframework.session.web.socket.server.SessionRepositoryMessageInterceptor.SPRING_SESSION_ID_ATTR_NAME
 * which is private, so I can't import it here.
 */
public class SessionIdCopyingHandshakeInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (attributes.containsKey(HTTP_SESSION_ID_ATTR_NAME)) {
            attributes.put("SPRING.SESSION.ID", attributes.get(HTTP_SESSION_ID_ATTR_NAME));
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // nothing to do here
    }
}
