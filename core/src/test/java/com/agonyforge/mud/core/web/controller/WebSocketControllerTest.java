package com.agonyforge.mud.core.web.controller;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.service.InMemoryUserRepository;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.agonyforge.mud.core.web.controller.WebSocketController.CURRENT_QUESTION_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.SESSION_ATTRIBUTES;

@ExtendWith(MockitoExtension.class)
public class WebSocketControllerTest {
    @Mock
    private Principal principal;

    @Mock
    private Question question;

    @Mock
    private Response response;

    @Mock
    private InMemoryUserRepository userRepository;

    private Map<String, Object> headers;

    @BeforeEach
    void setUp() {
        Mockito.reset(question, response);
        Map<String, Object> attributes = new HashMap<>();

        headers = new HashMap<>();

        attributes.put(CURRENT_QUESTION_KEY, question);
        headers.put(SESSION_ATTRIBUTES, attributes);
    }

    @Test
    void testSubscribe() {
        when(question.prompt(any())).thenReturn(new Output("", "[default]> "));

        WebSocketController uut = new WebSocketController(question, userRepository);
        Output result = uut.onSubscribe(principal, headers);

        assertEquals(3, result.getOutput().size());
        assertEquals("Welcome!", result.getOutput().get(0));
        assertEquals("", result.getOutput().get(1));
        assertEquals("[default]> ", result.getOutput().get(2));

        verify(userRepository).getWsSessionNames();
    }

    @Test
    void testSubscribeMissingAttributes() {
        headers.remove(SESSION_ATTRIBUTES);

        WebSocketController uut = new WebSocketController(question, userRepository);
        Output result = uut.onSubscribe(principal, headers);

        assertEquals(1, result.getOutput().size());
        assertEquals("[red]Oops! Something went wrong. Please try refreshing your browser.", result.getOutput().get(0));

        verify(userRepository, never()).getWsSessionNames();
    }

    @Test
    void testInput() {
        when(response.getNext()).thenReturn(question);
        when(response.getFeedback()).thenReturn(Optional.of(new Output("[cyan]You say, 'Hello![cyan]'")));
        when(question.answer(any(), any())).thenReturn(response);
        when(question.prompt(any())).thenReturn(new Output("", "[default]> "));

        Input input = new Input("Hello!");
        WebSocketController uut = new WebSocketController(question, userRepository);
        Output result = uut.onInput(principal, input, headers);

        assertEquals(3, result.getOutput().size());
        assertEquals("[cyan]You say, 'Hello![cyan]'", result.getOutput().get(0));
        assertEquals("", result.getOutput().get(1));
        assertEquals("[default]> ", result.getOutput().get(2));
    }

    @Test
    void testInputMissingAttributes() {
        headers.remove(SESSION_ATTRIBUTES);

        Input input = new Input("Hello!");
        WebSocketController uut = new WebSocketController(question, userRepository);
        Output result = uut.onInput(principal, input, headers);

        assertEquals(1, result.getOutput().size());
        assertEquals("[red]Oops! Something went wrong. Please try again.", result.getOutput().get(0));
    }
}
