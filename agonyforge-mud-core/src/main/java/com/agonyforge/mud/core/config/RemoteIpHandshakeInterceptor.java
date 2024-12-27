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

    private final String remoteIpHeader;

    public RemoteIpHandshakeInterceptor(String remoteIpHeader) {
        this.remoteIpHeader = remoteIpHeader;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        Optional
            .ofNullable(request.getHeaders().get(remoteIpHeader))
            .ifPresent(headers -> headers.forEach(header -> Arrays.stream(header.split(","))
                .map(address -> {
                    try {
                        InetAddress inetAddress = InetAddress.getByName(address);

                        return inetAddress.getHostAddress();
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
