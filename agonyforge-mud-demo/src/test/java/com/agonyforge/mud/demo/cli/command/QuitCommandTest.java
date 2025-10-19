package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.CharacterComponent;
import com.agonyforge.mud.demo.model.impl.LocationComponent;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudRoom;
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
import java.util.Random;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuitCommandTest {
    private static final Random RAND = new Random();

    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private CommService commService;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private WebSocketContext webSocketContext;

    @Mock
    private Question question, menuQuestion;

    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private MudRoom room;

    @Mock
    private MudCharacter ch;

    @Mock
    private LocationComponent locationComponent;

    @Mock
    private CharacterComponent characterComponent;

    @Mock
    private Binding commandBinding, nowBinding;

    @BeforeEach
    void setUp() {
        lenient().when(applicationContext.getBean(eq("characterMenuQuestion"), eq(Question.class))).thenReturn(menuQuestion);
        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        lenient().when(ch.getCharacter()).thenReturn(characterComponent);
        lenient().when(ch.getLocation()).thenReturn(locationComponent);
        lenient().when(locationComponent.getRoom()).thenReturn(room);
    }

    @Test
    void testQuitWrongArgs() {
        Output output = new Output();
        QuitCommand uut = new QuitCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(question, webSocketContext, List.of(commandBinding, nowBinding), output);

        when(nowBinding.asString()).thenReturn("later");

        assertEquals(question, result);
        assertTrue(output.getOutput().stream().anyMatch(s -> s.contains("You must type 'quit now'.")));

        verify(characterRepository, never()).save(eq(ch));
        verify(commService, never()).sendToAll(eq(webSocketContext), any(Output.class), eq(ch));
    }

    @Test
    void testQuitNotFullyTyped() {
        Output output = new Output();
        QuitCommand uut = new QuitCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(question, webSocketContext, List.of(commandBinding, nowBinding), output);

        when(nowBinding.asString()).thenReturn("n");

        assertEquals(question, result);
        assertTrue(output.getOutput().stream().anyMatch(s -> s.contains("You must type 'quit now'.")));

        verify(characterRepository, never()).save(eq(ch));
        verify(commService, never()).sendToAll(eq(webSocketContext), any(Output.class), eq(ch));
    }

    @Test
    void testQuit() {
        Long chId = RAND.nextLong();

        when(nowBinding.asString()).thenReturn("now");
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));

        Output output = new Output();
        QuitCommand uut = new QuitCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(question, webSocketContext, List.of(commandBinding, nowBinding), output);

        assertEquals(menuQuestion, result);
        assertTrue(output.getOutput().stream().anyMatch(s -> s.contains("Goodbye!")));

        verify(characterRepository).save(eq(ch));
        verify(commService).sendToAll(eq(webSocketContext), any(Output.class), eq(ch));
    }
}
