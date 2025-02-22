package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.CommandReference;
import com.agonyforge.mud.demo.model.repository.CommandRepository;
import com.agonyforge.mud.demo.service.CommService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommandEditorCommandTest {
    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private CommService commService;

    @Mock
    private CommandRepository commandRepository;

    @Mock
    private Question question;

    @Mock
    private WebSocketContext webSocketContext;

    @Mock
    private CommandReference commandRef;

    @Mock
    private Command command;

    @Captor
    private ArgumentCaptor<CommandReference> commandRefCaptor;

    @BeforeEach
    void setUp() {
        lenient().when(commandRef.getPriority()).thenReturn(1);
        lenient().when(commandRef.getName()).thenReturn("TEST");
        lenient().when(commandRef.getBeanName()).thenReturn("testCommand");
        lenient().when(commandRef.getDescription()).thenReturn("Tests things.");

        lenient().when(commandRepository.findByNameIgnoreCase(eq("test"))).thenReturn(Optional.of(commandRef));
        lenient().when(commandRepository.findAll()).thenReturn(List.of(commandRef));
        lenient().when(commandRepository.save(any(CommandReference.class))).thenAnswer(i -> i.getArguments()[0]);

        lenient().when(applicationContext.getBean(eq("testCommand"), eq(Command.class))).thenReturn(command);
        lenient().when(applicationContext.getBean(eq("missingCommand"), eq(Command.class))).thenThrow(new NoSuchBeanDefinitionException("Aw nuts!"));
    }

    @Test
    void testNoArgs() {
        Output output = new Output();
        CommandEditorCommand uut = new CommandEditorCommand(repositoryBundle, commandRepository, commService, applicationContext);
        Question result = uut.execute(question, webSocketContext, List.of("CEDIT"), new Input("cedit"), output);

        assertEquals(question, result);
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("Pri")));
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("Command")));
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("Bean Name")));
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("Description")));
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("Tests things.")));

        verify(commandRepository, never()).save(any(CommandReference.class));
        verify(commandRepository, never()).delete(any(CommandReference.class));
    }

    @Test
    void testInvalidSubcommand() {
        Output output = new Output();
        CommandEditorCommand uut = new CommandEditorCommand(repositoryBundle, commandRepository, commService, applicationContext);
        Question result = uut.execute(question, webSocketContext, List.of("CEDIT", "TEST"), new Input("cedit test"), output);

        assertEquals(question, result);
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("Invalid subcommand.")));

        verify(commandRepository, never()).save(any(CommandReference.class));
        verify(commandRepository, never()).delete(any(CommandReference.class));
    }

    @Test
    void testCreate() {
        Output output = new Output();
        CommandEditorCommand uut = new CommandEditorCommand(repositoryBundle, commandRepository, commService, applicationContext);
        Question result = uut.execute(question, webSocketContext,
            List.of("CEDIT", "CREATE", "TEST", "50", "TESTCOMMAND", "TESTS", "STUFF"),
            new Input("cedit create test 50 testCommand Tests stuff"),
            output);

        assertEquals(question, result);
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("Created TEST command")));

        verify(commandRepository).save(commandRefCaptor.capture());
        verify(commandRepository, never()).delete(any(CommandReference.class));

        CommandReference capturedCommand = commandRefCaptor.getValue();

        assertEquals("TEST", capturedCommand.getName());
        assertEquals(50, capturedCommand.getPriority());
        assertEquals("testCommand", capturedCommand.getBeanName());
        assertEquals("Tests stuff", capturedCommand.getDescription());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "cedit create",
        "cedit create test",
        "cedit create test 50",
        "cedit create test fifty",
        "cedit create test 50 testCommand",
        "cedit create test fifty testCommand"
    })
    void testCreateWrongArgs(String input) {
        Output output = new Output();
        CommandEditorCommand uut = new CommandEditorCommand(repositoryBundle, commandRepository, commService, applicationContext);
        Question result = uut.execute(question, webSocketContext,
            Arrays.stream(StringUtils.tokenizeToStringArray(input.toUpperCase(Locale.ROOT), " ", true, true)).toList(),
            new Input(input),
            output);

        assertEquals(question, result);
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("CEDIT CREATE")));

        verify(commandRepository, never()).save(any(CommandReference.class));
        verify(commandRepository, never()).delete(any(CommandReference.class));
    }

    @Test
    void testCreateWrongPriority() {
        Output output = new Output();
        CommandEditorCommand uut = new CommandEditorCommand(repositoryBundle, commandRepository, commService, applicationContext);
        Question result = uut.execute(question, webSocketContext,
            List.of("CEDIT", "CREATE", "TEST", "FIFTY", "TESTCOMMAND", "DESCRIPTION"),
            new Input("cedit create test fifty testCommand description"),
            output);

        assertEquals(question, result);
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("Priority must be a number")));

        verify(commandRepository, never()).save(any(CommandReference.class));
        verify(commandRepository, never()).delete(any(CommandReference.class));
    }

    @Test
    void testCreateMissingBean() {
        Output output = new Output();
        CommandEditorCommand uut = new CommandEditorCommand(repositoryBundle, commandRepository, commService, applicationContext);
        Question result = uut.execute(question, webSocketContext,
            List.of("CEDIT", "CREATE", "TEST", "50", "MISSINGCOMMAND", "TESTS", "STUFF"),
            new Input("cedit create test 50 missingCommand Tests stuff"),
            output);

        assertEquals(question, result);
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("No command bean could be found with that name")));

        verify(commandRepository, never()).save(any(CommandReference.class));
        verify(commandRepository, never()).delete(any(CommandReference.class));
    }

    @Test
    void testDelete() {
        Output output = new Output();
        CommandEditorCommand uut = new CommandEditorCommand(repositoryBundle, commandRepository, commService, applicationContext);
        Question result = uut.execute(question, webSocketContext,
            List.of("CEDIT", "DELETE", "TEST"),
            new Input("cedit delete test"),
            output);

        assertEquals(question, result);

        verify(commandRepository, never()).save(any(CommandReference.class));
        verify(commandRepository).delete(any(CommandReference.class));
    }

    @Test
    void testDeleteMissingCommand() {
        Output output = new Output();
        CommandEditorCommand uut = new CommandEditorCommand(repositoryBundle, commandRepository, commService, applicationContext);
        Question result = uut.execute(question, webSocketContext,
            List.of("CEDIT", "DELETE", "WRONG"),
            new Input("cedit delete wrong"),
            output);

        assertEquals(question, result);
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("Unknown command")));

        verify(commandRepository, never()).save(any(CommandReference.class));
        verify(commandRepository, never()).delete(any(CommandReference.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "cedit delete",
        "cedit delete test foo"
    })
    void testDeleteWrongArgs(String input) {
        Output output = new Output();
        CommandEditorCommand uut = new CommandEditorCommand(repositoryBundle, commandRepository, commService, applicationContext);
        Question result = uut.execute(question, webSocketContext,
            Arrays.stream(StringUtils.tokenizeToStringArray(input.toUpperCase(Locale.ROOT), " ", true, true)).toList(),
            new Input(input),
            output);

        assertEquals(question, result);
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("CEDIT DELETE")));

        verify(commandRepository, never()).save(any(CommandReference.class));
        verify(commandRepository, never()).delete(any(CommandReference.class));
    }
}
