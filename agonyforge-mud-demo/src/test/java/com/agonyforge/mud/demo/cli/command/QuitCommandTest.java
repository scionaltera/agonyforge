package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.model.impl.CharacterComponent;
import com.agonyforge.mud.demo.model.impl.LocationComponent;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudRoom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
public class QuitCommandTest extends CommandTestBoilerplate {
    private static final Random RAND = new Random();

    @Mock
    private Question question, menuQuestion;

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
        lenient().when(ch.getCharacter()).thenReturn(characterComponent);
        lenient().when(ch.getLocation()).thenReturn(locationComponent);
        lenient().when(locationComponent.getRoom()).thenReturn(room);
    }

    @Test
    void testQuitWrongArgs() {
        when(nowBinding.asString()).thenReturn("later");

        Output output = new Output();
        QuitCommand uut = new QuitCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(question, webSocketContext, List.of(commandBinding, nowBinding), output);

        assertEquals(question, result);
        assertTrue(output.getOutput().stream().anyMatch(s -> s.contains("You must type 'quit now'.")));

        verify(characterRepository, never()).save(eq(ch));
        verify(commService, never()).sendToAll(eq(webSocketContext), any(Output.class), eq(ch));
    }

    @Test
    void testQuitNotFullyTyped() {
        when(nowBinding.asString()).thenReturn("n");

        Output output = new Output();
        QuitCommand uut = new QuitCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(question, webSocketContext, List.of(commandBinding, nowBinding), output);

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
