package com.agonyforge.mud.core.service;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;

import java.security.Principal;
import java.util.List;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_QUESTION;
import static com.agonyforge.mud.core.web.controller.WebSocketController.WS_SESSION_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EchoServiceTest {
    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private FindByIndexNameSessionRepository<Session> sessionRepository;

    @Mock
    private SessionRegistry sessionRegistry;

    @Mock
    private OAuth2AuthenticationToken senderPrincipal;

    @Mock
    private WebAuthenticationDetails details;

    @Mock
    private DefaultOidcUser senderOidc;

    @Mock
    private SessionInformation senderSessionInfo;

    @Mock
    private Session senderHttpSession;

    @Mock
    private DefaultOidcUser targetOidc;

    @Mock
    private SessionInformation targetSessionInfo;

    @Mock
    private Session targetHttpSession;

    @Mock
    private Question question;

    @Captor
    private ArgumentCaptor<MessageHeaders> headersCaptor;

    @SuppressWarnings("FieldCanBeLocal")
    private final String senderWsSessionId = "ijklmnop";
    private final String targetWsSessionId = "abcdefgh";

    @BeforeEach
    void setUp() {
        String senderHttpSessionId = "senderHttpSessionId";
        String targetHttpSessionId = "targetHttpSessionId";

        when(senderPrincipal.getDetails()).thenReturn(details);
        when(details.getSessionId()).thenReturn(senderHttpSessionId);

        lenient().when(senderOidc.getName()).thenReturn("Alice");
        when(senderSessionInfo.getSessionId()).thenReturn(senderHttpSessionId);
        lenient().when(senderHttpSession.getAttribute(eq(MUD_QUESTION))).thenReturn("currentQuestion");
        lenient().when(senderHttpSession.getAttribute(eq(WS_SESSION_ID))).thenReturn(senderWsSessionId);
        when(senderHttpSession.getId()).thenReturn(senderHttpSessionId);

        when(targetOidc.getName()).thenReturn("Bob");
        when(targetSessionInfo.getSessionId()).thenReturn(targetHttpSessionId);
        when(targetHttpSession.getAttribute(eq(MUD_QUESTION))).thenReturn("currentQuestion");
        when(targetHttpSession.getAttribute(eq(WS_SESSION_ID))).thenReturn(targetWsSessionId);
        when(targetHttpSession.getId()).thenReturn(targetHttpSessionId);

        when(sessionRegistry.getAllPrincipals()).thenReturn(List.of(senderOidc, targetOidc));
        when(sessionRegistry.getAllSessions(eq(senderOidc), anyBoolean())).thenReturn(List.of(senderSessionInfo));
        when(sessionRegistry.getAllSessions(eq(targetOidc), anyBoolean())).thenReturn(List.of(targetSessionInfo));

        when(sessionRepository.findById(eq(senderHttpSessionId))).thenReturn(senderHttpSession);
        when(sessionRepository.findById(eq(targetHttpSessionId))).thenReturn(targetHttpSession);

        when(applicationContext.getBean(eq("currentQuestion"), eq(Question.class))).thenReturn(question);
        when(question.prompt(any(Principal.class), any())).thenReturn(new Output("", "[default]> "));
    }

    @Test
    void testEchoToAll() {
        EchoService uut = new EchoService(
            applicationContext,
            simpMessagingTemplate,
            sessionRepository,
            sessionRegistry);

        uut.echoToAll(senderPrincipal, new Output("Testy McTestface"));

        verify(simpMessagingTemplate, times(1)).convertAndSendToUser(
            eq("Bob"),
            eq("/queue/output"),
            eq(new Output("Testy McTestface", "", "[default]> ")),
            headersCaptor.capture());

        MessageHeaders headers = headersCaptor.getValue();
        assertEquals(targetWsSessionId, headers.get(SimpMessageHeaderAccessor.SESSION_ID_HEADER));

        verify(simpMessagingTemplate, times(1)).convertAndSendToUser(
            anyString(),
            anyString(),
            any(Output.class),
            any(MessageHeaders.class));
    }
}
