package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.demo.model.impl.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WhoCommandTest extends CommandTestBoilerplate {
    @Mock
    private MudCharacter other;

    @Mock
    private PlayerComponent chPlayerComponent, otherPlayerComponent;

    @Mock
    private CharacterComponent chCharacterComponent, otherCharacterComponent;

    @Mock
    private LocationComponent chLocationComponent, otherLocationComponent;

    @Test
    void testExecuteOnePlayer() {
        List<MudCharacter> characters = List.of(ch);
        Output output = new Output();

        when(chCharacterComponent.getName()).thenReturn("Scion");
        when(chPlayerComponent.getTitle()).thenReturn("the Man With the Plan");
        when(ch.getCharacter()).thenReturn(chCharacterComponent);
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getPlayer()).thenReturn(chPlayerComponent);
        when(characterRepository.findAll()).thenReturn(characters);

        WhoCommand uut = new WhoCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(question, webSocketContext, List.of(commandBinding), output);

        assertEquals(question, result);

        assertTrue(output.getOutput().get(0).contains("Who is Playing"));
        assertTrue(output.getOutput().get(2).contains("Scion the Man With the Plan"));
        assertTrue(output.getOutput().get(4).contains("1 player online"));
    }

    @Test
    void testExecuteTwoPlayer() {
        List<MudCharacter> characters = List.of(ch, other);
        Output output = new Output();

        when(chCharacterComponent.getName()).thenReturn("Scion");
        when(chPlayerComponent.getTitle()).thenReturn("the Man With the Plan");
        when(otherCharacterComponent.getName()).thenReturn("Spook");
        when(otherPlayerComponent.getTitle()).thenReturn("the Other Man With the Other Plan");
        when(ch.getCharacter()).thenReturn(chCharacterComponent);
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getPlayer()).thenReturn(chPlayerComponent);
        when(other.getCharacter()).thenReturn(otherCharacterComponent);
        when(other.getLocation()).thenReturn(otherLocationComponent);
        when(other.getPlayer()).thenReturn(otherPlayerComponent);
        when(characterRepository.findAll()).thenReturn(characters);

        WhoCommand uut = new WhoCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(question, webSocketContext, List.of(commandBinding), output);

        assertEquals(question, result);

        assertTrue(output.getOutput().get(0).contains("Who is Playing"));
        assertTrue(output.getOutput().get(2).contains("Scion the Man With the Plan"));
        assertTrue(output.getOutput().get(3).contains("Spook the Other Man With the Other Plan"));
        assertTrue(output.getOutput().get(5).contains("2 players online"));
    }
}
