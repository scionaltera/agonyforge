package com.agonyforge.mud.demo.cli.command;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.model.impl.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import com.agonyforge.mud.core.cli.Question;
import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import com.agonyforge.mud.demo.service.CommService;

@ExtendWith(MockitoExtension.class)
class ForceCommandTest {
    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private CommService commService;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private WebSocketContext webSocketContext;

    @Mock
    private Question question;

    @Mock
    private MudCharacter executor, target;

    @Mock
    private CharacterComponent executorComponent, targetComponent;

    @Mock
    private LocationComponent locationComponent;

    @Mock
    private MudRoom room;

    @Mock
    private CommandReference sayRef, forceRef;

    @Mock
    private Binding commandBinding, targetBinding, forcedCommandBinding, argsBinding;

    private ForceCommand uut;
    private final Random random = new Random();

    @BeforeEach
    void setUp() {
        uut = new ForceCommand(repositoryBundle, commService, applicationContext);

        when(targetBinding.asCharacter()).thenReturn(target);
        when(argsBinding.asString()).thenReturn("Hello");
    }

    @Test
    void testForceSuccess() {
        // --- arrange ---
        MudCharacterRepository repo = mock(MudCharacterRepository.class);
        when(repositoryBundle.getCharacterRepository()).thenReturn(repo);
        when(forcedCommandBinding.asCommandReference()).thenReturn(sayRef);

        // executor in room
        long execId = random.nextLong();
        when(webSocketContext.getAttributes()).thenReturn(Map.of(MUD_CHARACTER, execId));
        when(repo.findById(execId)).thenReturn(Optional.of(executor));
        when(executor.getLocation()).thenReturn(locationComponent);
        when(locationComponent.getRoom()).thenReturn(room);

        // room contains executor + target
        when(repo.findByLocationRoom(room)).thenReturn(List.of(executor, target));

        // names
        when(executor.getCharacter()).thenReturn(executorComponent);
        when(executorComponent.getName()).thenReturn("Executor");
        when(target.getCharacter()).thenReturn(targetComponent);
        when(targetComponent.getName()).thenReturn("Bob");
        // when(target.getLocation()).thenReturn(locationComponent);

        // SAY command bean exists
        // when(applicationContext.containsBean("sayCommand")).thenReturn(true);
        // when(applicationContext.getBean("sayCommand")).thenReturn(dummyCommand);

        // --- act ---
        Output out = new Output();
        Question res = uut.execute(question, webSocketContext,
                List.of(commandBinding, targetBinding, forcedCommandBinding, argsBinding), out);

        // --- assert ---
        assertSame(question, res);
        assertEquals(
                "[yellow]You forced Bob to 'say Hello[yellow]'!",
                out.toString().trim());
        verify(commService).sendTo(eq(target), any(Output.class));
        verify(commService).executeCommandAs(webSocketContext, target, "say Hello");
    }

    @Test
    void testForceCommandWithNestedForce() {
        when(forcedCommandBinding.asCommandReference()).thenReturn(forceRef);

        Output output = new Output();
        Question result = uut.execute(question, webSocketContext, List.of(commandBinding, targetBinding, forcedCommandBinding, argsBinding), output);

        assertSame(question, result);
        assertEquals(
                "[default]You cannot force someone to force others!",
                output.toString().trim());
        verifyNoInteractions(repositoryBundle, applicationContext, commService);
    }
}
