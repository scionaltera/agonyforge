package com.agonyforge.mud.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class RemoteIpHandshakeInterceptor implements HandshakeInterceptor {
    public static final String SESSION_REMOTE_IP_KEY = "MUD.REMOTE.IP";

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteIpHandshakeInterceptor.class);
    private static final String X_FORWARDED_FOR_HEADER = "x-forwarded-for"; // TODO externalize configuration, allow a list of headers and trusted proxies

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        Optional
            .ofNullable(request.getHeaders().get(X_FORWARDED_FOR_HEADER))
            .ifPresent(headers -> headers.forEach(header -> Arrays.stream(header.split(","))
                .map(address -> {
                    try {
                        InetAddress inetAddress = InetAddress.getByName(address);

                        if (!inetAddress.isSiteLocalAddress()) {
                            return inetAddress.getHostAddress();
                        }
                    } catch (UnknownHostException e) {
                        LOGGER.debug("Failed to resolve IP for address: {}", address);
                    }

                    return null;
                })
                .filter(Objects::nonNull)
                .distinct()
                .findFirst()
                .ifPresent(address -> attributes.put(SESSION_REMOTE_IP_KEY, address))));

        attributes.putIfAbsent(SESSION_REMOTE_IP_KEY, request.getRemoteAddress().getAddress().getHostAddress());

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // nothing to do here
    }
}
