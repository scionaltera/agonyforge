package com.agonyforge.mud.core.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;

import static com.agonyforge.mud.core.config.RemoteIpHandshakeInterceptor.SESSION_REMOTE_IP_KEY;
import static org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor.HTTP_SESSION_ID_ATTR_NAME;

@Component
public class SessionDisconnectListener implements ApplicationListener<SessionDisconnectEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionDisconnectListener.class);

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> attributes = headerAccessor.getSessionAttributes();
        String username = headerAccessor.getUser() == null ? "(unknown user)" : headerAccessor.getUser().getName();
        String remoteIp = attributes == null ? "(unknown IP)" : (String)attributes.getOrDefault(SESSION_REMOTE_IP_KEY, "(unknown IP)");
        String httpSessionId = attributes == null ? "(unknown session)" : (String) attributes.get(HTTP_SESSION_ID_ATTR_NAME);

        LOGGER.info("Lost connection: {} {} {} {}",
            remoteIp,
            httpSessionId,
            headerAccessor.getSessionId(),
            username);
    }
}
