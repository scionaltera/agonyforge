package com.agonyforge.mud.core.service;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
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
import org.springframework.messaging.simp.user.SimpSession;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;

import java.security.Principal;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_QUESTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
    private SimpUserRegistry simpUserRegistry;

    @Mock
    private SessionAttributeService sessionAttributeService;

    @Mock
    private SimpUser sender;

    @Mock
    private SimpSession senderSession;

    @Mock
    private Principal senderPrincipal;

    @Mock
    private SimpUser target;

    @Mock
    private SimpSession targetSession;

    @Mock
    private Principal targetPrincipal;

    @Mock
    private WebSocketContext wsContext;

    @Mock
    private Question question;

    @Captor
    private ArgumentCaptor<MessageHeaders> headersCaptor;

    private final String senderSessionId = "senderSessionId";
    private final String targetSessionId = "targetSessionId";

    void setUpTwoPrincipals() {
        Map<String, Object> attributes = Map.of(
            MUD_QUESTION, "testQuestion"
        );

        when(wsContext.getSessionId()).thenReturn(senderSessionId);

        when(sender.getSessions()).thenReturn(Set.of(senderSession));
        when(senderSession.getId()).thenReturn(senderSessionId);

        when(target.getSessions()).thenReturn(Set.of(targetSession));
        when(target.getPrincipal()).thenReturn(targetPrincipal);
        when(targetPrincipal.getName()).thenReturn("Bob");
        when(targetSession.getId()).thenReturn(targetSessionId);
        when(targetSession.getUser()).thenReturn(target);

        when(sessionAttributeService.getSessionAttributes(eq(targetSessionId))).thenReturn(attributes);

        when(simpUserRegistry.getUsers()).thenReturn(Set.of(sender, target));
        when(question.prompt(any(WebSocketContext.class))).thenReturn(new Output(Arrays.asList("", "[default]> ")));
        when(applicationContext.getBean(eq("testQuestion"), eq(Question.class))).thenReturn(question);
    }

    @Test
    void testEchoToAll() {
        setUpTwoPrincipals();

        EchoService uut = new EchoService(
            applicationContext,
            simpMessagingTemplate,
            simpUserRegistry,
            sessionAttributeService);

        uut.echoToAll(wsContext, new Output("Testy McTestface"));

        verify(simpMessagingTemplate, times(1)).convertAndSendToUser(
            eq("Bob"),
            eq("/queue/output"),
            eq(new Output(Arrays.asList("Testy McTestface", "", "[default]> "))),
            headersCaptor.capture());

        MessageHeaders headers = headersCaptor.getValue();
        assertEquals(targetSessionId, headers.get(SimpMessageHeaderAccessor.SESSION_ID_HEADER));

        verify(simpMessagingTemplate, times(1)).convertAndSendToUser(
            anyString(),
            anyString(),
            any(Output.class),
            any(MessageHeaders.class));
    }

    void setUpSamePrincipal() {
        Map<String, Object> attributes = Map.of(
            MUD_QUESTION, "testQuestion"
        );

        when(wsContext.getSessionId()).thenReturn(senderSessionId);

        when(sender.getSessions()).thenReturn(Set.of(senderSession, targetSession));
        when(sender.getPrincipal()).thenReturn(senderPrincipal);
        when(senderPrincipal.getName()).thenReturn("Alice");
        when(senderSession.getId()).thenReturn(senderSessionId);

        when(targetSession.getId()).thenReturn(targetSessionId);
        when(targetSession.getUser()).thenReturn(sender);

        when(sessionAttributeService.getSessionAttributes(eq(targetSessionId))).thenReturn(attributes);

        when(simpUserRegistry.getUsers()).thenReturn(Set.of(sender));
        when(question.prompt(any(WebSocketContext.class))).thenReturn(new Output(Arrays.asList("", "[default]> ")));
        when(applicationContext.getBean(eq("testQuestion"), eq(Question.class))).thenReturn(question);
    }

    @Test
    void testEchoToAllSamePrincipal() {
        setUpSamePrincipal();

        EchoService uut = new EchoService(
            applicationContext,
            simpMessagingTemplate,
            simpUserRegistry,
            sessionAttributeService);

        uut.echoToAll(wsContext, new Output("Testy McTestface"));

        verify(simpMessagingTemplate, times(1)).convertAndSendToUser(
            eq("Alice"),
            eq("/queue/output"),
            eq(new Output(Arrays.asList("Testy McTestface", "", "[default]> "))),
            headersCaptor.capture());

        MessageHeaders headers = headersCaptor.getValue();
        assertEquals(targetSessionId, headers.get(SimpMessageHeaderAccessor.SESSION_ID_HEADER));

        verify(simpMessagingTemplate, times(1)).convertAndSendToUser(
            anyString(),
            anyString(),
            any(Output.class),
            any(MessageHeaders.class));
    }
}
