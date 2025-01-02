package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.*;
import com.agonyforge.mud.demo.model.repository.CommandRepository;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import com.agonyforge.mud.demo.service.CommService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HelpCommandTest {
    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private CommandRepository commandRepository;

    @Mock
    private CommService commService;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private Question question;

    @Mock
    private WebSocketContext wsContext;

    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private MudCharacterPrototype chProto;

    @Mock
    private MudCharacter ch;

    @Mock
    private LocationComponent chLocationComponent;

    @Mock
    private MudRoom room;

    @Mock
    private PlayerComponent playerComponent;

    @Mock
    private Role playerRole;

    @Mock
    private CommandReference testCommandRefA;

    @Mock
    private CommandReference testCommandRefB;

    @BeforeEach
    void setUp() {
        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        lenient().when(wsContext.getAttributes()).thenReturn(Map.of(MUD_CHARACTER, 1L));
        lenient().when(characterRepository.findById(eq(1L))).thenReturn(Optional.of(ch));

        lenient().when(testCommandRefA.getName()).thenReturn("TEST_A");
        lenient().when(testCommandRefA.getPriority()).thenReturn(10);
        lenient().when(testCommandRefA.getDescription()).thenReturn("Test Command A");

        lenient().when(testCommandRefB.getName()).thenReturn("TEST_B");
        lenient().when(testCommandRefB.getPriority()).thenReturn(20);
        lenient().when(testCommandRefB.getDescription()).thenReturn("Test Command B");

        lenient().when(commandRepository.findAll()).thenReturn(List.of(testCommandRefA, testCommandRefB));
        lenient().when(playerRole.getCommands()).thenReturn(Set.of(testCommandRefA));
        lenient().when(playerComponent.getRoles()).thenReturn(Set.of(playerRole));
        lenient().when(ch.getPlayer()).thenReturn(playerComponent);
    }

    @Test
    void testHelpSuper() {
        when(ch.getTemplate()).thenReturn(chProto);
        when(ch.getTemplate().getId()).thenReturn(1L);
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);

        HelpCommand uut = new HelpCommand(repositoryBundle, commService, applicationContext, commandRepository);
        Question result = uut.execute(question, wsContext, List.of("HELP"), new Input("help"), new Output());

        verify(testCommandRefA).getPriority();
        verify(testCommandRefA).getDescription();

        verify(testCommandRefB).getPriority();
        verify(testCommandRefB).getDescription();

        assertEquals(question, result);
    }

    @Test
    void testHelpPlayer() {
        when(ch.getTemplate()).thenReturn(chProto);
        when(ch.getTemplate().getId()).thenReturn(2L);
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);

        HelpCommand uut = new HelpCommand(repositoryBundle, commService, applicationContext, commandRepository);
        Question result = uut.execute(question, wsContext, List.of("HELP"), new Input("help"), new Output());

        verify(testCommandRefA).getDescription();

        verify(testCommandRefB, never()).getPriority();
        verify(testCommandRefB, never()).getDescription();

        assertEquals(question, result);
    }
}
