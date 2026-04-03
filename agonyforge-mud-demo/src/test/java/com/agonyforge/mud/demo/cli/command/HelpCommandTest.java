package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.model.impl.*;
import com.agonyforge.mud.demo.model.repository.CommandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HelpCommandTest extends CommandTestBoilerplate {
    @Mock
    private CommandRepository commandRepository;

    @Mock
    private Question question;

    @Mock
    private MudCharacter ch;

    @Mock
    private LocationComponent chLocationComponent;

    @Mock
    private MudRoom room;

    @Mock
    private PlayerComponent playerComponent;

    @Mock
    private Role implRole, playerRole;

    @Mock
    private CommandReference testCommandRefA, testCommandRefB;

    @Mock
    private Binding commandBinding;

    @BeforeEach
    void setUp() {
        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        lenient().when(webSocketContext.getAttributes()).thenReturn(Map.of(MUD_CHARACTER, 1L));
        lenient().when(characterRepository.findById(eq(1L))).thenReturn(Optional.of(ch));

        lenient().when(testCommandRefA.getName()).thenReturn("TEST_A");
        lenient().when(testCommandRefA.getPriority()).thenReturn(10);
        lenient().when(testCommandRefA.getDescription()).thenReturn("Test Command A");

        lenient().when(testCommandRefB.getName()).thenReturn("TEST_B");
        lenient().when(testCommandRefB.getPriority()).thenReturn(20);
        lenient().when(testCommandRefB.getDescription()).thenReturn("Test Command B");

        lenient().when(commandRepository.findAll()).thenReturn(List.of(testCommandRefA, testCommandRefB));
        lenient().when(playerRole.getCommands()).thenReturn(Set.of(testCommandRefA));
        lenient().when(ch.getPlayer()).thenReturn(playerComponent);
    }

    @Test
    void testHelpSuper() {
        when(implRole.isImplementor()).thenReturn(true);

        when(playerComponent.getRoles()).thenReturn(Set.of(implRole, playerRole));
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);

        HelpCommand uut = new HelpCommand(repositoryBundle, commService, applicationContext, commandRepository);
        Question result = uut.execute(question, webSocketContext, List.of(commandBinding), new Output());

        verify(testCommandRefA).getPriority();
        verify(testCommandRefA).getDescription();

        verify(testCommandRefB).getPriority();
        verify(testCommandRefB).getDescription();

        assertEquals(question, result);
    }

    @Test
    void testHelpPlayer() {
        when(playerComponent.getRoles()).thenReturn(Set.of(playerRole));
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);

        HelpCommand uut = new HelpCommand(repositoryBundle, commService, applicationContext, commandRepository);
        Question result = uut.execute(question, webSocketContext, List.of(commandBinding), new Output());

        verify(testCommandRefA).getDescription();

        verify(testCommandRefB, never()).getPriority();
        verify(testCommandRefB, never()).getDescription();

        assertEquals(question, result);
    }
}
