package com.agonyforge.mud.web.controller;

import com.agonyforge.mud.web.model.Input;
import com.agonyforge.mud.web.model.Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

import static com.agonyforge.mud.config.RemoteIpHandshakeInterceptor.SESSION_REMOTE_IP_KEY;

@Controller
public class WebSocketController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketController.class);

    @SubscribeMapping("/queue/output")
    public Output onSubscribe(Principal principal, Message<byte[]> message) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(message);
        Map<String, Object> attributes = headerAccessor.getSessionAttributes();
        String remoteIp = attributes == null ? "(unknown IP)" : (String)attributes.getOrDefault(SESSION_REMOTE_IP_KEY, "(unknown IP)");

        LOGGER.info("New connection: {} {} {}",
            remoteIp,
            headerAccessor.getSessionId(),
            principal.getName());

        return new Output("Welcome!");
    }

    @MessageMapping("/input")
    @SendToUser(value = "/queue/output", broadcast = false)
    public Output onInput(Input input, Message<byte[]> message) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(message);
        Map<String, Object> attributes = headerAccessor.getSessionAttributes();

        String payload = input.getInput();

        LOGGER.info("Message: {}", payload);
        return new Output("Echo: " + payload);
    }
}
