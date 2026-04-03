package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.service.SessionAttributeService;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.cli.TokenType;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GotoCommandTest extends CommandTestBoilerplate {
    @Mock
    private SessionAttributeService sessionAttributeService;

    @Mock
    private MudCharacter target;

    @Mock
    private LocationComponent chLocation, targetLocation;

    @Mock
    private CharacterComponent chCharacter, targetCharacter;

    @Mock
    private MudRoom room, destination;

    @Mock
    private Binding targetBinding, roomBinding;

    @BeforeEach
    void setUp() {
        Long targetId = getRandom().nextLong();

        lenient().when(roomRepository.findById(3000L)).thenReturn(Optional.of(destination));

        lenient().when(ch.getLocation()).thenReturn(chLocation);
        lenient().when(chLocation.getRoom()).thenReturn(room);
        lenient().when(ch.getCharacter()).thenReturn(chCharacter);
        lenient().when(chCharacter.getName()).thenReturn("Scion");

        lenient().when(characterRepository.findById(eq(targetId))).thenReturn(Optional.of(target));
        lenient().when(target.getLocation()).thenReturn(targetLocation);
        lenient().when(targetLocation.getRoom()).thenReturn(destination);
        lenient().when(target.getCharacter()).thenReturn(targetCharacter);
        lenient().when(targetCharacter.getName()).thenReturn("Target");
    }

    @Test
    void testGotoPlayer() {
        when(targetLocation.getRoom()).thenReturn(destination);
        when(target.getLocation()).thenReturn(targetLocation);
        when(targetBinding.asCharacter()).thenReturn(target);
        when(targetBinding.getType()).thenReturn(TokenType.CHARACTER_IN_WORLD);

        Output output = new Output();
        GotoCommand uut = new GotoCommand(repositoryBundle, commService, applicationContext, sessionAttributeService);
        Question result = uut.execute(question, webSocketContext, List.of(commandBinding, targetBinding), output);

        assertEquals(question, result);
        verify(commService, times(2)).sendToRoom(anyLong(), any(Output.class), eq(ch));
        verify(chLocation).setRoom(eq(destination));
    }

    @Test
    void testGotoRoom() {
        when(roomBinding.asRoom()).thenReturn(destination);
        when(roomBinding.getType()).thenReturn(TokenType.ROOM_ID);

        Output output = new Output();
        GotoCommand uut = new GotoCommand(repositoryBundle, commService, applicationContext, sessionAttributeService);
        Question result = uut.execute(question, webSocketContext, List.of(commandBinding, roomBinding), output);

        assertEquals(question, result);
        verify(commService, times(2)).sendToRoom(anyLong(), any(Output.class), eq(ch));
        verify(chLocation).setRoom(eq(destination));
    }
}
