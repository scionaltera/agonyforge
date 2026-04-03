package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.model.impl.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TitleCommandTest extends CommandTestBoilerplate {
    @Mock
    private Question question;

    @Mock
    private MudCharacter ch;

    @Mock
    private MudRoom room;

    @Mock
    private CharacterComponent characterComponent;

    @Mock
    private PlayerComponent playerComponent;

    @Mock
    private LocationComponent locationComponent;

    @Mock
    private Binding commandBinding;

    @Mock
    private Binding titleBinding;

    @BeforeEach
    void setUp() {
        lenient().when(ch.getPlayer()).thenReturn(playerComponent);
        lenient().when(ch.getCharacter()).thenReturn(characterComponent);
        lenient().when(ch.getLocation()).thenReturn(locationComponent);

        lenient().when(locationComponent.getRoom()).thenReturn(room);

        lenient().when(webSocketContext.getAttributes()).thenReturn(Map.of(MUD_CHARACTER, 3L));

        lenient().when(characterRepository.findById(eq(3L))).thenReturn(Optional.of(ch));
    }

    @Test
    void testNormal() {
        when(titleBinding.asString()).thenReturn("the Amazing");

        TitleCommand uut = new TitleCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(question, webSocketContext, List.of(commandBinding, titleBinding), new Output());

        assertEquals(question, result);
        verify(playerComponent).setTitle(eq("the Amazing"));
    }

    @Test
    void testLongWithColors() {
        when(titleBinding.asString()).thenReturn("the [red]Amazing [dyellow]Amazing [yellow]Amazing [green]Amazing [blue]Amazing [magenta]Amazing");

        TitleCommand uut = new TitleCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(question, webSocketContext,
            List.of(commandBinding, titleBinding),
            new Output());

        assertEquals(question, result);
        verify(playerComponent).setTitle(eq("the [red]Amazing [dyellow]Amazing [yellow]Amazing [green]Amazing [blue]Amazing [magenta]Amazing"));
    }

    @Test
    void testTooLong() {
        when(titleBinding.asString()).thenReturn("the Amazing Amazing Amazing Amazing Amazing Amazing Amazing Amazing Amazing Amazing Amazing Amazing Amazing Amazing Amazing Amazing Amazing Amazing Amazing Amazing");

        TitleCommand uut = new TitleCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(question, webSocketContext,
            List.of(commandBinding, titleBinding),
            new Output());

        assertEquals(question, result);
        verify(playerComponent, never()).setTitle(anyString());
    }
}
