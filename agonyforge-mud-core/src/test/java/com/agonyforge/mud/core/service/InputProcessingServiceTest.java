package com.agonyforge.mud.core.service;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.Optional;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_QUESTION;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InputProcessingServiceTest {
    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private WebSocketContext wsContext;

    @Mock
    private Question currentQuestion;

    @Mock
    private Question nextQuestion;

    @Mock
    private Response response;

    @Mock
    private Map<String, Object> attributes;

    private InputProcessingService inputProcessingService;

    @BeforeEach
    void setUp() {
        inputProcessingService = new InputProcessingService(applicationContext);
    }

    @Test
    void testProcessInputWithFeedback() {
        // Setup
        String questionName = "testQuestion";
        String prompt = "Test prompt";
        String feedback = "Test feedback";
        Input input = new Input("test input");

        when(wsContext.getAttributes()).thenReturn(attributes);
        when(attributes.get(MUD_QUESTION)).thenReturn(questionName);
        when(applicationContext.getBean(questionName, Question.class)).thenReturn(currentQuestion);
        when(currentQuestion.answer(wsContext, input)).thenReturn(response);
        when(response.getNext()).thenReturn(nextQuestion);
        when(response.getFeedback()).thenReturn(Optional.of(new Output(feedback)));
        when(nextQuestion.prompt(wsContext)).thenReturn(new Output(prompt));
        when(nextQuestion.getBeanName()).thenReturn("nextQuestion");

        // Execute
        Output result = inputProcessingService.processInput(wsContext, input);

        // Verify
        assertNotNull(result);
        assertTrue(result.getOutput().contains(feedback));
        assertTrue(result.getOutput().contains(prompt));

        verify(attributes).put(MUD_QUESTION, "nextQuestion");
    }

    @Test
    void testProcessInputWithoutFeedback() {
        // Setup
        String questionName = "testQuestion";
        String prompt = "Test prompt";
        Input input = new Input("test input");

        when(wsContext.getAttributes()).thenReturn(attributes);
        when(attributes.get(MUD_QUESTION)).thenReturn(questionName);
        when(applicationContext.getBean(questionName, Question.class)).thenReturn(currentQuestion);
        when(currentQuestion.answer(wsContext, input)).thenReturn(response);
        when(response.getNext()).thenReturn(nextQuestion);
        when(response.getFeedback()).thenReturn(java.util.Optional.empty());
        when(nextQuestion.prompt(wsContext)).thenReturn(new Output(prompt));
        when(nextQuestion.getBeanName()).thenReturn("nextQuestion");

        // Execute
        Output result = inputProcessingService.processInput(wsContext, input);

        // Verify
        assertNotNull(result);
        assertFalse(result.getOutput().contains("Test feedback"));
        assertTrue(result.getOutput().contains(prompt));
        verify(attributes).put(MUD_QUESTION, "nextQuestion");
    }
}
