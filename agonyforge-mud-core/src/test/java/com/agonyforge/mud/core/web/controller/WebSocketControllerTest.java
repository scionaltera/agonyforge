package com.agonyforge.mud.core.web.controller;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.agonyforge.mud.core.config.RemoteIpHandshakeInterceptor.SESSION_REMOTE_IP_KEY;
import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_QUESTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.SESSION_ATTRIBUTES;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.SESSION_ID_HEADER;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.USER_HEADER;
import static org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor.HTTP_SESSION_ID_ATTR_NAME;

@ExtendWith(MockitoExtension.class)
public class WebSocketControllerTest {
    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private Question question;

    @Mock
    private Response response;

    @Mock
    private Principal principal;

    @Mock
    private WebSocketContextAware webSocketContextAware;

    @Captor
    private ArgumentCaptor<WebSocketContext> contextCaptor;

    private Map<String, Object> headers;

    @BeforeEach
    void setUp() {
        Map<String, Object> attributes = new HashMap<>();

        headers = new HashMap<>();

        attributes.put(SESSION_REMOTE_IP_KEY, "999.999.999.999");
        attributes.put(HTTP_SESSION_ID_ATTR_NAME, "httpSessionId");
        attributes.put(MUD_QUESTION, "testQuestion");

        headers.put(SESSION_ATTRIBUTES, attributes);
        headers.put(SESSION_ID_HEADER, "sessionId");
        headers.put(USER_HEADER, principal);
    }

    @Test
    void testSubscribe() {
        when(question.prompt(any(WebSocketContext.class))).thenReturn(new Output("").append("[default]> "));
        when(question.getBeanName()).thenReturn("testQuestion");

        WebSocketController uut = new WebSocketController(applicationContext, webSocketContextAware, question);
        Output result = uut.onSubscribe(headers);

        assertEquals(3, result.getOutput().size());
        assertEquals("Welcome!", result.getOutput().get(0));
        assertEquals("", result.getOutput().get(1));
        assertEquals("[default]> ", result.getOutput().get(2));

        verify(question).prompt(contextCaptor.capture());

        WebSocketContext ctx = contextCaptor.getValue();

        assertEquals("testQuestion", ctx.getAttributes().get(MUD_QUESTION));
    }

    @Test
    void testSubscribeMissingAttributes() {
        headers.remove(SESSION_ATTRIBUTES);

        WebSocketController uut = new WebSocketController(applicationContext, webSocketContextAware, question);
        Output result = uut.onSubscribe(headers);

        assertEquals(1, result.getOutput().size());
        assertTrue(result.getOutput().get(0).contains("[red]"));
        assertTrue(result.getOutput().get(0).contains("error"));

        verify(question, never ()).prompt(any(WebSocketContext.class));
    }

    @Test
    void testInput() {
        when(applicationContext.getBean(eq("testQuestion"), eq(Question.class))).thenReturn(question);
        when(response.getNext()).thenReturn(question);
        when(response.getFeedback()).thenReturn(Optional.of(new Output("[cyan]You say, 'Hello![cyan]'")));
        when(question.answer(any(WebSocketContext.class), any(Input.class))).thenReturn(response);
        when(question.prompt(any(WebSocketContext.class))).thenReturn(new Output("").append("[default]> "));
        when(question.getBeanName()).thenReturn("nextQuestion");

        Input input = new Input("Hello!");
        WebSocketController uut = new WebSocketController(applicationContext, webSocketContextAware, question);
        Output result = uut.onInput(headers, input);

        assertEquals("[cyan]You say, 'Hello![cyan]'", result.getOutput().get(0));
        assertEquals("", result.getOutput().get(1));
        assertEquals("[default]> ", result.getOutput().get(2));
        assertEquals(3, result.getOutput().size());

        verify(question).prompt(contextCaptor.capture());

        WebSocketContext ctx = contextCaptor.getValue();

        assertEquals("nextQuestion", ctx.getAttributes().get(MUD_QUESTION));
    }

    @Test
    void testInputMissingAttributes() {
        headers.remove(SESSION_ATTRIBUTES);

        Input input = new Input("Hello!");
        WebSocketController uut = new WebSocketController(applicationContext, webSocketContextAware, question);
        Output result = uut.onInput(headers, input);

        assertEquals(1, result.getOutput().size());
        assertTrue(result.getOutput().get(0).contains("[red]"));
        assertTrue(result.getOutput().get(0).contains("error"));

        verify(question, never ()).prompt(any(WebSocketContext.class));
    }
}
