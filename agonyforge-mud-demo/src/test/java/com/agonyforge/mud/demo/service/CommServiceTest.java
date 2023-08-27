package com.agonyforge.mud.demo.service;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.service.SessionAttributeService;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_QUESTION;
import static com.agonyforge.mud.demo.model.impl.ModelConstants.TYPE_PC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommServiceTest {
    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private SimpUserRegistry simpUserRegistry;

    @Mock
    private SessionAttributeService sessionAttributeService;

    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private MudCharacter ch;

    @Mock
    private MudCharacter target;

    @Mock
    private MudCharacter other;

    @Mock
    private MudCharacter prototype;

    @Mock
    private Question question;

    @Mock
    private WebSocketContext webSocketContext;

    @Captor
    private ArgumentCaptor<Output> outputCaptor;

    private final String principal = "principal";
    private final String targetPrincipal = "target";
    private final String otherPrincipal = "other";
    private final String wsSessionId = UUID.randomUUID().toString();
    private final String targetWsSessionId = UUID.randomUUID().toString();
    private final String otherWsSessionId = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_QUESTION, "nextQuestion");

        when(question.prompt(any(WebSocketContext.class))).thenReturn(new Output(Arrays.asList("", "[default]> ")));
        lenient().when(sessionAttributeService.getSessionAttributes(eq(wsSessionId))).thenReturn(attributes);
        lenient().when(sessionAttributeService.getSessionAttributes(eq(targetWsSessionId))).thenReturn(attributes);
        lenient().when(sessionAttributeService.getSessionAttributes(eq(otherWsSessionId))).thenReturn(attributes);
        when(applicationContext.getBean(eq("nextQuestion"), eq(Question.class))).thenReturn(question);

        lenient().when(webSocketContext.getSessionId()).thenReturn(wsSessionId);

        lenient().when(characterRepository.getByRoom(eq(100L))).thenReturn(List.of(ch, target));
        lenient().when(characterRepository.getByType(eq(TYPE_PC))).thenReturn(List.of(ch, target, other, prototype));

        lenient().when(ch.getUser()).thenReturn(principal);
        when(ch.getWebSocketSession()).thenReturn(wsSessionId);
        lenient().when(ch.getRoomId()).thenReturn(100L);

        lenient().when(target.getUser()).thenReturn(targetPrincipal);
        lenient().when(target.getWebSocketSession()).thenReturn(targetWsSessionId);
        lenient().when(target.getRoomId()).thenReturn(100L);

        lenient().when(other.getUser()).thenReturn(otherPrincipal);
        lenient().when(other.getWebSocketSession()).thenReturn(otherWsSessionId);
        lenient().when(other.getRoomId()).thenReturn(200L);

        lenient().when(prototype.isPrototype()).thenReturn(true);
        lenient().when(prototype.getUser()).thenReturn(otherPrincipal);
        lenient().when(prototype.getWebSocketSession()).thenThrow(new IllegalStateException());
        lenient().when(prototype.getRoomId()).thenThrow(new IllegalStateException());
    }

    @Test
    void testSendToAllWebsocket() {
        CommService uut = new CommService(
            applicationContext,
            simpMessagingTemplate,
            simpUserRegistry,
            sessionAttributeService,
            characterRepository);

        Output message = new Output("message");

        uut.sendToAll(webSocketContext, message);

        verify(simpMessagingTemplate).convertAndSendToUser(
            eq(targetPrincipal),
            eq("/queue/output"),
            any(Output.class),
            any(MessageHeaders.class)
        );

        verify(simpMessagingTemplate).convertAndSendToUser(
            eq(otherPrincipal),
            eq("/queue/output"),
            any(Output.class),
            any(MessageHeaders.class)
        );

        verifyNoMoreInteractions(simpMessagingTemplate);
    }

    @Test
    void testSendToAll() {
        CommService uut = new CommService(
            applicationContext,
            simpMessagingTemplate,
            simpUserRegistry,
            sessionAttributeService,
            characterRepository);

        Output message = new Output("message");

        uut.sendToAll(message);

        verify(simpMessagingTemplate).convertAndSendToUser(
            eq(principal),
            eq("/queue/output"),
            any(Output.class),
            any(MessageHeaders.class)
        );

        verify(simpMessagingTemplate).convertAndSendToUser(
            eq(targetPrincipal),
            eq("/queue/output"),
            any(Output.class),
            any(MessageHeaders.class)
        );

        verify(simpMessagingTemplate).convertAndSendToUser(
            eq(otherPrincipal),
            eq("/queue/output"),
            any(Output.class),
            any(MessageHeaders.class)
        );

        verifyNoMoreInteractions(simpMessagingTemplate);
    }

    @Test
    void testSendToZone() {
        CommService uut = new CommService(
            applicationContext,
            simpMessagingTemplate,
            simpUserRegistry,
            sessionAttributeService,
            characterRepository);

        Output message = new Output("message");

        uut.sendToZone(webSocketContext,1L, message);

        verify(simpMessagingTemplate).convertAndSendToUser(
            eq(targetPrincipal),
            eq("/queue/output"),
            any(Output.class),
            any(MessageHeaders.class)
        );

        verifyNoMoreInteractions(simpMessagingTemplate);
    }

    @Test
    void testSendToRoom() {
        CommService uut = new CommService(
            applicationContext,
            simpMessagingTemplate,
            simpUserRegistry,
            sessionAttributeService,
            characterRepository);

        Output message = new Output("message");

        uut.sendToRoom(webSocketContext,100L, message);

        verify(simpMessagingTemplate).convertAndSendToUser(
            eq(targetPrincipal),
            eq("/queue/output"),
            any(Output.class),
            any(MessageHeaders.class)
        );

        verifyNoMoreInteractions(simpMessagingTemplate);
    }

    @Test
    void testSendToTargets() {
        CommService uut = new CommService(
            applicationContext,
            simpMessagingTemplate,
            simpUserRegistry,
            sessionAttributeService,
            characterRepository);

        Output message = new Output("message");

        uut.sendToTargets(List.of(ch, target, prototype), message);

        verify(simpMessagingTemplate).convertAndSendToUser(
            eq(principal),
            eq("/queue/output"),
            any(Output.class),
            any(MessageHeaders.class)
        );

        verify(simpMessagingTemplate).convertAndSendToUser(
            eq(targetPrincipal),
            eq("/queue/output"),
            any(Output.class),
            any(MessageHeaders.class)
        );

        verifyNoMoreInteractions(simpMessagingTemplate);
    }

    @Test
    void testSendTo() {
        CommService uut = new CommService(
            applicationContext,
            simpMessagingTemplate,
            simpUserRegistry,
            sessionAttributeService,
            characterRepository);

        Output message = new Output("message");

        uut.sendTo(ch, message);

        verify(simpMessagingTemplate).convertAndSendToUser(
            eq(principal),
            eq("/queue/output"),
            outputCaptor.capture(),
            any(MessageHeaders.class)
        );

        verifyNoMoreInteractions(simpMessagingTemplate);

        Output output = outputCaptor.getValue();

        assertEquals(3, output.getOutput().size());
        assertEquals("message", output.getOutput().get(0));
        assertEquals("", output.getOutput().get(1));
        assertEquals("[default]> ", output.getOutput().get(2));
    }
}
