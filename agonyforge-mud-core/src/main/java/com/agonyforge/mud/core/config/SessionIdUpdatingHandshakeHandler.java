package com.agonyforge.mud.core.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

/*
 * The default behavior is for the Principal from the HTTP session to get copied into the WebSocket
 * session. Different sessions for the same Principal are differentiated by having different
 * HTTP session IDs (UUIDs) and unique WebSocket session IDs (like "ifyfjrxm").
 *
 * The problem was that Spring Security rotates your session when you authenticate to foil
 * session fixation attacks, but it doesn't put the new ID in this Principal. So later on when
 * I'm trying to look up sessions it's a whole mess because even when I do find the ID it doesn't
 * match anything in the SessionRepository!
 *
 * This handler builds a new WebAuthenticationDetails object based on the current request's updated
 * session ID and sticks it on the Principal before returning it. That way all the session IDs do
 * match up like they're supposed to.
 */
public class SessionIdUpdatingHandshakeHandler extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        ServletServerHttpRequest servletServerHttpRequest = (ServletServerHttpRequest) request;
        HttpServletRequest httpRequest = servletServerHttpRequest.getServletRequest();
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) httpRequest.getUserPrincipal();
        WebAuthenticationDetails newDetails = new WebAuthenticationDetails(httpRequest);

        token.setDetails(newDetails);

        return token;
    }
}
