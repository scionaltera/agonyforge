package com.agonyforge.mud.core.service;

import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InputProcessingServiceTest {

    private InputProcessingService inputProcessingService;

    @Mock
    private WebSocketContext webSocketContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        inputProcessingService = new InputProcessingService();
    }

    @Test
    void testProcessInputWithValidInput() {
        // Arrange
        String inputString = "look";
        Input input = new Input(inputString);
        Output output = new Output();

        // Act
        Output result = inputProcessingService.processInput(webSocketContext, input);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testProcessInputWithEmptyInput() {
        // Arrange
        Input input = new Input("");
        Output output = new Output();

        // Act
        Output result = inputProcessingService.processInput(webSocketContext, input);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testProcessInputWithNullInput() {
        // Arrange
        Input input = new Input(null);
        Output output = new Output();

        // Act
        Output result = inputProcessingService.processInput(webSocketContext, input);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testProcessInputWithWhitespaceOnlyInput() {
        // Arrange
        Input input = new Input("   ");
        Output output = new Output();

        // Act
        Output result = inputProcessingService.processInput(webSocketContext, input);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testProcessInputWithSpecialCharacters() {
        // Arrange
        Input input = new Input("!@#$%^&*()");
        Output output = new Output();

        // Act
        Output result = inputProcessingService.processInput(webSocketContext, input);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}
