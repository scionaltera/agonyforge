package com.agonyforge.mud.core.service;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Component;

import java.security.Principal;

import static com.agonyforge.mud.core.web.controller.WebSocketController.CURRENT_QUESTION_KEY;
import static com.agonyforge.mud.core.web.controller.WebSocketController.WS_SESSION_ID;

/*
 * TODO Not writing tests for this one yet because it's very likely to change.
 */
@Component
public class EchoService {
    private final ApplicationContext applicationContext;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final FindByIndexNameSessionRepository<Session> sessionRepository;
    private final SessionRegistry sessionRegistry;

    @Autowired
    public EchoService(ApplicationContext applicationContext,
                       SimpMessagingTemplate simpMessagingTemplate,
                       FindByIndexNameSessionRepository<Session> sessionRepository,
                       SessionRegistry sessionRegistry) {
        this.applicationContext = applicationContext;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.sessionRepository = sessionRepository;
        this.sessionRegistry = sessionRegistry;
    }

    /**
     * Low level method to send a message to everyone except the sender.
     *
     * @param senderPrincipal The Principal representing the sender of the message.
     * @param message The message to be sent.
     */
    public void echoToAll(Principal senderPrincipal, Output message) {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) senderPrincipal;
        WebAuthenticationDetails details = (WebAuthenticationDetails) token.getDetails();
        String senderSessionId = details.getSessionId();

        sessionRegistry.getAllPrincipals()
            .stream()
            .map(principal -> (DefaultOidcUser) principal)
            .forEach(principal -> sessionRegistry.getAllSessions(principal, false)
                .stream()
                .map(sessionInfo -> sessionRepository.findById(sessionInfo.getSessionId()))
                .filter(session -> !senderSessionId.equals(session.getId()))
                .forEach(session -> {
                    Principal stompPrincipal = new StompPrincipal(principal.getName());
                    Output messageWithPrompt = appendPrompt(stompPrincipal, session, message);
                    String wsSessionId = session.getAttribute(WS_SESSION_ID);
                    MessageHeaders messageHeaders = buildMessageHeaders(wsSessionId);

                    simpMessagingTemplate.convertAndSendToUser(
                        stompPrincipal.getName(),
                        "/queue/output",
                        messageWithPrompt,
                        messageHeaders);
                }));
    }

    /**
     * Look up the target user's current Question and use it to build the correct prompt for them.
     *
     * @param stompPrincipal A Principal containing the WS session ID of the target user.
     * @param session The HTTP Session of the target user.
     * @param message The message to send.
     * @return An Output with a prompt appended to it.
     */
    private Output appendPrompt(Principal stompPrincipal, Session session, Output message) {
        Question question = applicationContext.getBean(session.getAttribute(CURRENT_QUESTION_KEY), Question.class);

        return new Output(message).append(question.prompt(stompPrincipal, session));
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
