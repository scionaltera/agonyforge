package com.agonyforge.mud.core.service;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Map;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_QUESTION;

@Component
public class EchoService {
    private final ApplicationContext applicationContext;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final SimpUserRegistry simpUserRegistry;
    private final SessionAttributeService sessionAttributeService;

    @Autowired
    public EchoService(ApplicationContext applicationContext,
                       SimpMessagingTemplate simpMessagingTemplate,
                       SimpUserRegistry simpUserRegistry,
                       SessionAttributeService sessionAttributeService) {
        this.applicationContext = applicationContext;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.simpUserRegistry = simpUserRegistry;
        this.sessionAttributeService = sessionAttributeService;
    }

    /**
     * Low level method to send a message to everyone except the sender.
     *
     * @param wsContext The WebSocketContext for the sender.
     * @param message The message to be sent.
     */
    public void echoToAll(WebSocketContext wsContext, Output message) {
        simpUserRegistry.getUsers()
                .stream()
                .flatMap(simpUser -> simpUser.getSessions().stream())
                .filter(simpSession -> !simpSession.getId().equals(wsContext.getSessionId()))
                .forEach(simpSession -> {
                    Principal targetPrincipal = simpSession.getUser().getPrincipal();
                    Map<String, Object> attributes = sessionAttributeService.getSessionAttributes(simpSession.getId());
                    WebSocketContext targetWsContext = WebSocketContext.build(targetPrincipal, simpSession.getId(), attributes);

                    Output messageWithPrompt = appendPrompt(targetWsContext, message);
                    MessageHeaders messageHeaders = buildMessageHeaders(simpSession.getId());

                    simpMessagingTemplate.convertAndSendToUser(
                        targetWsContext.getPrincipal().getName(),
                        "/queue/output",
                        messageWithPrompt,
                        messageHeaders);
                });
    }

    /**
     * Look up the target user's current Question and use it to build the correct prompt for them.
     *
     * @param wsContext The WebSocketContext for the target.
     * @param message The message to send.
     * @return An Output with a prompt appended to it.
     */
    private Output appendPrompt(WebSocketContext wsContext, Output message) {
        Question question = applicationContext.getBean((String) wsContext.getAttributes().get(MUD_QUESTION), Question.class);

        return new Output(message).append(question.prompt(wsContext));
    }

    /**
     * Build a MessageHeaders that will only send the STOMP message to the one connection we targeted.
     * Does the same thing as setting broadcast = false in the @SendToUser annotation. Without this
     * it would broadcast to every connection from the same Principal.
     *
     * @param wsSessionId WebSocket session ID.
     * @return A MessageHeaders designed to limit the message to only the specified session ID.
     */
    private MessageHeaders buildMessageHeaders(String wsSessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
        headerAccessor.setSessionId(wsSessionId);
        return headerAccessor.getMessageHeaders();
    }
}
