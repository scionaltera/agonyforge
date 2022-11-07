package com.agonyforge.mud.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

import static org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor.HTTP_SESSION_ID_ATTR_NAME;

/*
 * The default behavior is to copy the HTTP session's Principal name into the WebSocket session's
 * Principal, which gets you something like loginwithamazon_amzn1.account.***************************
 *
 * That's great and works in a lot of use cases. I think a lot of times you'd want it so that you
 * can connect a second browser or tab and see the same view as the first one. In our case though we
 * don't want to have multiple browsers in the same MUD session (i.e. both controlling the same
 * character at the same time... it would be so weird!) so we need to make the WebSocket Principals
 * more unique.
 *
 * In earlier versions I assigned a random UUID but this time I decided to try using the HTTP Session ID
 * instead. It's unique per browser instead of per account, and we have access to the WebSocket
 * Principal in a lot of places in the code pretty easily. The main downside is that now the HTTP Principal
 * and WebSocket Principal have different names in them, so it's important to know which one you have.
 */
public class SessionIdHandshakeHandler extends DefaultHandshakeHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionIdCopyingHandshakeInterceptor.class);
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String sessionId = (String)attributes.get(HTTP_SESSION_ID_ATTR_NAME);

        LOGGER.debug("Assigning HTTP Session ID as STOMP Principal: {}", sessionId);

        return new StompPrincipal(sessionId);
    }
}
