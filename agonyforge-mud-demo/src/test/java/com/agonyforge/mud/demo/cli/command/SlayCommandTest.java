package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.cli.TokenType;
import com.agonyforge.mud.demo.model.constant.Pronoun;
import com.agonyforge.mud.demo.model.impl.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SlayCommandTest extends CommandTestBoilerplate {
    @Mock
    private Output output;

    @Mock
    private MudCharacter target;

    @Mock
    private LocationComponent chLocation;

    @Mock
    private CharacterComponent chComponent, targetComponent;

    @Mock
    private MudRoom room;

    private final long ROOM_ID = getRandom().nextLong();

    @BeforeEach
    void setUp() {
        when(ch.getLocation()).thenReturn(chLocation);
        when(ch.getCharacter()).thenReturn(chComponent);
        when(chLocation.getRoom()).thenReturn(room);
        when(chComponent.getName()).thenReturn("Scion");
        when(chComponent.getPronoun()).thenReturn(Pronoun.HE);

        when(target.getCharacter()).thenReturn(targetComponent);
        when(targetComponent.getName()).thenReturn("Target");

        when(room.getId()).thenReturn(ROOM_ID);
    }

    @Test
    public void testSlay() {
        SlayCommand uut = new SlayCommand(repositoryBundle, commService, applicationContext);
        Binding binding = new Binding(TokenType.NPC_IN_ROOM, "target", target);
        Question result = uut.execute(question, webSocketContext, List.of(commandBinding, binding), output);

        assertEquals(question, result);

        verify(characterRepository).delete(eq(target));
        verify(characterRepository, never()).delete(eq(ch));

        verify(commService).sendToRoom(eq(ROOM_ID), any(Output.class), eq(ch));
    }
}
