package com.agonyforge.mud.web.controller;

import com.agonyforge.mud.web.model.Input;
import com.agonyforge.mud.web.model.Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.Headers;
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
    public Output onSubscribe(Principal principal, @Headers Map<String, Object> headers) {
        Map<String, Object> attributes = SimpMessageHeaderAccessor.getSessionAttributes(headers);
        String remoteIp = attributes == null ? "(no IP)" : (String)attributes.getOrDefault(SESSION_REMOTE_IP_KEY, "(no IP)");
        String sessionId = SimpMessageHeaderAccessor.getSessionId(headers);

        LOGGER.info("New connection: {} {} {}",
            remoteIp,
            sessionId,
            principal.getName());

        // TODO Show greeting.
        // TODO Select initial state, bust a prompt.

        return new Output("Welcome!");
    }

    @MessageMapping("/input")
    @SendToUser(value = "/queue/output", broadcast = false)
    public Output onInput(Input input, @Headers Map<String, Object> headers) {
        /*
         * The phases here should work like this:
         *
         * 0) Set initial state; ask a question. (in onSubscribe)
         * 1) Process the response.
         * 2) Optionally produce some output. (e.g. an error message)
         * 3) Move to the next state.
         * 4) Ask a question. (i.e. a prompt or a menu)
         * 5) GOTO 1
         */

        String payload = input.getInput();

        LOGGER.info("Message: {}", payload);
        return new Output("Echo: " + payload);
    }
}
