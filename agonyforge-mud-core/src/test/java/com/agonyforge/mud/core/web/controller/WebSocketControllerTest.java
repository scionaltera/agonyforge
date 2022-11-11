package com.agonyforge.mud.core.web.controller;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.agonyforge.mud.core.web.controller.WebSocketController.CURRENT_QUESTION_KEY;
import static com.agonyforge.mud.core.web.controller.WebSocketController.WS_SESSION_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.SESSION_ATTRIBUTES;
import static org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor.HTTP_SESSION_ID_ATTR_NAME;

@ExtendWith(MockitoExtension.class)
public class WebSocketControllerTest {
    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private FindByIndexNameSessionRepository<Session> sessionRepository;

    @Mock
    private Principal principal;

    @Mock
    private Question question;

    @Mock
    private Response response;

    @Mock
    private Session session;

    private Map<String, Object> headers;

    @BeforeEach
    void setUp() {
        Map<String, Object> attributes = new HashMap<>();

        headers = new HashMap<>();

        attributes.put(HTTP_SESSION_ID_ATTR_NAME, "sessionId");
        attributes.put(CURRENT_QUESTION_KEY, "testQuestion");
        headers.put(SESSION_ATTRIBUTES, attributes);
    }

    @Test
    void testSubscribe() {
        when(sessionRepository.findById(eq("sessionId"))).thenReturn(session);
        when(question.prompt(any(Principal.class), any(Session.class))).thenReturn(new Output("", "[default]> "));
        when(question.getBeanName()).thenReturn("testQuestion");

        WebSocketController uut = new WebSocketController(applicationContext, sessionRepository, question);
        Output result = uut.onSubscribe(principal, headers);

        assertEquals(3, result.getOutput().size());
        assertEquals("Welcome!", result.getOutput().get(0));
        assertEquals("", result.getOutput().get(1));
        assertEquals("[default]> ", result.getOutput().get(2));

        verify(session).setAttribute(eq(CURRENT_QUESTION_KEY), eq("testQuestion"));
        verify(session).setAttribute(eq(WS_SESSION_ID), eq(null));
    }

    @Test
    void testSubscribeMissingAttributes() {
        headers.remove(SESSION_ATTRIBUTES);

        WebSocketController uut = new WebSocketController(applicationContext, sessionRepository, question);
        Output result = uut.onSubscribe(principal, headers);

        assertEquals(1, result.getOutput().size());
        assertEquals("[red]Oops! Something went wrong. Please try refreshing your browser.", result.getOutput().get(0));

        verify(session, never()).setAttribute(any(), any());
    }

    @Test
    void testInput() {
        when(sessionRepository.findById(eq("sessionId"))).thenReturn(session);
        when(session.getAttribute(eq(CURRENT_QUESTION_KEY))).thenReturn("testQuestion");
        when(applicationContext.getBean(eq("testQuestion"), eq(Question.class))).thenReturn(question);
        when(response.getNext()).thenReturn(question);
        when(response.getFeedback()).thenReturn(Optional.of(new Output("[cyan]You say, 'Hello![cyan]'")));
        when(question.answer(any(Principal.class), any(Session.class), any(Input.class))).thenReturn(response);
        when(question.prompt(any(Principal.class), any(Session.class))).thenReturn(new Output("", "[default]> "));
        when(question.getBeanName()).thenReturn("nextQuestion");

        Input input = new Input("Hello!");
        WebSocketController uut = new WebSocketController(applicationContext, sessionRepository, question);
        Output result = uut.onInput(principal, input, headers);

        assertEquals(3, result.getOutput().size());
        assertEquals("[cyan]You say, 'Hello![cyan]'", result.getOutput().get(0));
        assertEquals("", result.getOutput().get(1));
        assertEquals("[default]> ", result.getOutput().get(2));

        verify(session).setAttribute(eq(CURRENT_QUESTION_KEY), eq("nextQuestion"));
    }

    @Test
    void testInputMissingAttributes() {
        headers.remove(SESSION_ATTRIBUTES);

        Input input = new Input("Hello!");
        WebSocketController uut = new WebSocketController(applicationContext, sessionRepository, question);
        Output result = uut.onInput(principal, input, headers);

        assertEquals(1, result.getOutput().size());
        assertEquals("[red]Oops! Something went wrong. Please try again.", result.getOutput().get(0));

        verify(session, never()).setAttribute(any(), any());
    }
}
