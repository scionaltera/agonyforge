package com.agonyforge.mud.demo.cli.command;

import java.util.List;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
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
    private MudCharacter target;

    @Mock
    private CharacterComponent targetComponent;

    @Mock
    private CommandReference sayRef, forceRef;

    @Mock
    private Binding commandBinding, targetBinding, forcedCommandBinding, argsBinding;

    private ForceCommand uut;

    @BeforeEach
    void setUp() {
        uut = new ForceCommand(repositoryBundle, commService, applicationContext);

        when(targetBinding.asCharacter()).thenReturn(target);
        when(argsBinding.asString()).thenReturn("Hello");
    }

    @Test
    void testForceSuccess() {
        // --- arrange ---
        when(forcedCommandBinding.asCommandReference()).thenReturn(sayRef);
        when(sayRef.getBeanName()).thenReturn("sayCommand");
        when(sayRef.getName()).thenReturn("SAY");
        when(target.getCharacter()).thenReturn(targetComponent);
        when(targetComponent.getName()).thenReturn("Bob");

        // --- act ---
        Output out = new Output();
        Question res = uut.execute(question, webSocketContext,
                List.of(commandBinding, targetBinding, forcedCommandBinding, argsBinding), out);

        // --- assert ---
        assertSame(question, res);
        assertEquals(
                "[yellow]You FORCE Bob to 'SAY Hello[yellow]'!",
                out.toString().trim());
        verify(commService).sendTo(eq(target), any(Output.class));
        verify(commService).executeCommandAs(webSocketContext, target, "SAY Hello");
    }

    @Test
    void testForceCommandWithNestedForce() {
        when(forcedCommandBinding.asCommandReference()).thenReturn(forceRef);
        when(forceRef.getBeanName()).thenReturn("forceCommand");

        Output output = new Output();
        Question result = uut.execute(question, webSocketContext, List.of(commandBinding, targetBinding, forcedCommandBinding, argsBinding), output);

        assertSame(question, result);
        assertEquals(
                "[default]You cannot force someone to force others!",
                output.toString().trim());
        verifyNoInteractions(repositoryBundle, applicationContext, commService);
    }
}
