package com.agonyforge.mud.demo.cli.command;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import com.agonyforge.mud.demo.service.CommService;

@ExtendWith(MockitoExtension.class)
class ForceCommandTest {
    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private CommService commService;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private WebSocketContext webSocketContext;

    @Mock
    private Question question;

    @Mock
    private MudCharacter executor;

    private ForceCommand uut;
    private final Random random = new Random();

    @BeforeEach
    void setUp() {
        uut = new ForceCommand(repositoryBundle, commService, applicationContext);
    }

    @Test
    void testForceMissingTarget() {
        List<String> tokens = List.of("FORCE");
        Output output = new Output();

        Question result = uut.execute(question, webSocketContext, tokens, new Input("force"), output);

        assertEquals(question, result);
        assertEquals("[default]Who would you like to force?", output.toString().trim());
    }

    @Test
    void testForceMissingCommand() {
        List<String> tokens = List.of("FORCE", "Bob");
        Output output = new Output();

        Question result = uut.execute(question, webSocketContext, tokens, new Input("force Bob"), output);

        assertEquals(question, result);
        assertEquals("[default]What would you like to force them to do?", output.toString().trim());
    }

    @Test
    void testCommandDoesNotExist() {
        List<String> tokens = List.of("FORCE", "Bob", "dance");
        Output output = new Output();

        when(applicationContext.containsBean("danceCommand")).thenReturn(false);

        Question result = uut.execute(question, webSocketContext, tokens, new Input("force Bob dance"), output);

        assertEquals(question, result);
        assertEquals("[default]That command does not exist: dance", output.toString().trim());
    }
}