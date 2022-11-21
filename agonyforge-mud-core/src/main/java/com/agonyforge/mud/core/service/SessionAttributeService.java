package com.agonyforge.mud.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.ChannelInterceptor;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SessionAttributeService implements ChannelInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionAttributeService.class);

    private final Set<SimpMessageType> putMessageTypes = EnumSet.of(
        SimpMessageType.CONNECT,
        SimpMessageType.SUBSCRIBE,
        SimpMessageType.MESSAGE);
    private final Set<SimpMessageType> removeMessageTypes = EnumSet.of(
        SimpMessageType.UNSUBSCRIBE,
        SimpMessageType.DISCONNECT);

    private final Map<String, Map<String, Object>> allSessionAttributes = new HashMap<>();

    public Map<String, Object> getSessionAttributes(String sessionId) {
        return allSessionAttributes.getOrDefault(sessionId, new HashMap<>());
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        SimpMessageType messageType = SimpMessageHeaderAccessor.getMessageType(message.getHeaders());

        if (putMessageTypes.contains(messageType)) {
            Map<String, Object> attributes = SimpMessageHeaderAccessor.getSessionAttributes(message.getHeaders());
            String sessionId = SimpMessageHeaderAccessor.getSessionId(message.getHeaders());

            LOGGER.debug("Storing session attributes for {}", sessionId);
            allSessionAttributes.put(sessionId, attributes);
        }

        if (removeMessageTypes.contains(messageType)) {
            String sessionId = SimpMessageHeaderAccessor.getSessionId(message.getHeaders());

            LOGGER.debug("Removing session attributes for {}", sessionId);
            allSessionAttributes.remove(sessionId);
        }

        return message;
    }
}
