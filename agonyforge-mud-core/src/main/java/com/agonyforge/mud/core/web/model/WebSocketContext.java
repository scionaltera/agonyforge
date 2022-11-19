package com.agonyforge.mud.core.web.model;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WebSocketContext {
    private final Principal principal;
    private final String sessionId;
    private final Map<String, Object> attributes;

    public static WebSocketContext build(Map<String, Object> headers) throws IllegalStateException {
        WebSocketContext instance = new WebSocketContext(headers);

        verify(instance);

        return instance;
    }

    public static WebSocketContext build(Principal principal, String sessionId, Map<String, Object> attributes) {
        WebSocketContext instance = new WebSocketContext(principal, sessionId, attributes);

        verify(instance);

        return instance;
    }

    private static void verify(WebSocketContext instance) throws IllegalStateException {
        List<String> errors = new ArrayList<>();

        if (instance.getPrincipal() == null) {
            errors.add("no Principal");
        }

        if (instance.getSessionId() == null) {
            errors.add("no Session ID");
        }

        if (instance.getAttributes() == null) {
            errors.add("no Session Attributes");
        }

        if (errors.size() > 0) {
            throw new IllegalStateException(String.join(",", errors));
        }
    }

    private WebSocketContext(Principal principal, String sessionId, Map<String, Object> attributes) {
        this.principal = principal;
        this.sessionId = sessionId;
        this.attributes = attributes;
    }

    private WebSocketContext(Map<String, Object> headers) {
        this.principal = SimpMessageHeaderAccessor.getUser(headers);
        this.sessionId = SimpMessageHeaderAccessor.getSessionId(headers);
        this.attributes = SimpMessageHeaderAccessor.getSessionAttributes(headers);
    }

    public Principal getPrincipal() {
        return principal;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
