package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.CommandReference;
import com.agonyforge.mud.demo.model.repository.CommandRepository;
import com.agonyforge.mud.demo.service.CommService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import java.util.List;
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

    @Mock
    private Binding commandBinding, subCommandBinding, commandNameBinding, priorityBinding, beanNameBinding, descriptionBinding;

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
        Question result = uut.execute(question, webSocketContext, List.of(commandBinding), output);

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
        Question result = uut.execute(question, webSocketContext, List.of(commandBinding, subCommandBinding), output);

        assertEquals(question, result);
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("Invalid subcommand.")));

        verify(commandRepository, never()).save(any(CommandReference.class));
        verify(commandRepository, never()).delete(any(CommandReference.class));
    }

    @Test
    void testCreate() {
        when(subCommandBinding.asString()).thenReturn("create");
        when(commandNameBinding.asString()).thenReturn("TEST");
        when(priorityBinding.asString()).thenReturn("50");
        when(beanNameBinding.asString()).thenReturn("testCommand");
        when(descriptionBinding.asString()).thenReturn("Tests stuff");

        Output output = new Output();
        CommandEditorCommand uut = new CommandEditorCommand(repositoryBundle, commandRepository, commService, applicationContext);
        Question result = uut.execute(question, webSocketContext,
            List.of(commandBinding, subCommandBinding, commandNameBinding, priorityBinding, beanNameBinding, descriptionBinding),
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

    @Test
    void testCreateMissingBean() {
        when(subCommandBinding.asString()).thenReturn("create");
        when(commandNameBinding.asString()).thenReturn("TEST");
        when(beanNameBinding.asString()).thenReturn("missingCommand");
        when(descriptionBinding.asString()).thenReturn("Tests stuff");

        Output output = new Output();
        CommandEditorCommand uut = new CommandEditorCommand(repositoryBundle, commandRepository, commService, applicationContext);
        Question result = uut.execute(question, webSocketContext,
            List.of(commandBinding, subCommandBinding, commandNameBinding, priorityBinding, beanNameBinding, descriptionBinding),
            output);

        assertEquals(question, result);
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("No command bean could be found with that name")));

        verify(commandRepository, never()).save(any(CommandReference.class));
        verify(commandRepository, never()).delete(any(CommandReference.class));
    }

    @Test
    void testDelete() {
        when(subCommandBinding.asString()).thenReturn("delete");
        when(commandNameBinding.asString()).thenReturn("TEST");

        Output output = new Output();
        CommandEditorCommand uut = new CommandEditorCommand(repositoryBundle, commandRepository, commService, applicationContext);
        Question result = uut.execute(question, webSocketContext,
            List.of(commandBinding, subCommandBinding, commandNameBinding),
            output);

        assertEquals(question, result);

        verify(commandRepository, never()).save(any(CommandReference.class));
        verify(commandRepository).delete(any(CommandReference.class));
    }

    @Test
    void testDeleteMissingCommand() {
        when(subCommandBinding.asString()).thenReturn("delete");
        when(commandNameBinding.asString()).thenReturn("WRONG");

        Output output = new Output();
        CommandEditorCommand uut = new CommandEditorCommand(repositoryBundle, commandRepository, commService, applicationContext);
        Question result = uut.execute(question, webSocketContext,
            List.of(commandBinding, subCommandBinding, commandNameBinding),
            output);

        assertEquals(question, result);
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("Unknown command")));

        verify(commandRepository, never()).save(any(CommandReference.class));
        verify(commandRepository, never()).delete(any(CommandReference.class));
    }
}
