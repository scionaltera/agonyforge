package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.service.SessionAttributeService;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.model.constant.AdminFlag;
import com.agonyforge.mud.demo.model.constant.Pronoun;
import com.agonyforge.mud.demo.model.impl.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransferCommandTest extends CommandTestBoilerplate {
    @Mock
    private SessionAttributeService sessionAttributeService;

    @Mock
    private MudCharacter target;

    @Mock
    private LocationComponent chLocation, targetLocation;

    @Mock
    private CharacterComponent chCharacter, targetCharacter;

    @Mock
    private PlayerComponent chPlayer, targetPlayer;

    @Mock
    private MudRoom room, destination;

    @Mock
    private Binding targetBinding;

    @BeforeEach
    void setUp() {
        Long targetId = getRandom().nextLong();
        Long destinationId = 3000L;

        lenient().when(roomRepository.findById(destinationId)).thenReturn(Optional.of(destination));

        lenient().when(ch.getLocation()).thenReturn(chLocation);
        lenient().when(chLocation.getRoom()).thenReturn(destination);
        lenient().when(ch.getCharacter()).thenReturn(chCharacter);
        lenient().when(chCharacter.getName()).thenReturn("Scion");
        lenient().when(ch.getPlayer()).thenReturn(chPlayer);
        lenient().when(chPlayer.getAdminFlags()).thenReturn(EnumSet.noneOf(AdminFlag.class));

        lenient().when(characterRepository.findById(eq(targetId))).thenReturn(Optional.of(target));
        lenient().when(target.getLocation()).thenReturn(targetLocation);
        lenient().when(targetLocation.getRoom()).thenReturn(room);
        lenient().when(target.getCharacter()).thenReturn(targetCharacter);
        lenient().when(targetCharacter.getName()).thenReturn("Target");
        lenient().when(targetCharacter.getPronoun()).thenReturn(Pronoun.THEY);
        lenient().when(target.getPlayer()).thenReturn(targetPlayer);
        lenient().when(targetPlayer.getAdminFlags()).thenReturn(EnumSet.noneOf(AdminFlag.class));

        when(targetBinding.asCharacter()).thenReturn(target);
    }

    @Test
    void testTransferPlayerSameRoom() {
        lenient().when(targetLocation.getRoom()).thenReturn(destination);

        Output output = new Output();
        TransferCommand uut = new TransferCommand(repositoryBundle, commService, applicationContext, sessionAttributeService);
        Question result = uut.execute(question, webSocketContext, List.of(commandBinding, targetBinding), output);

        assertEquals(question, result);
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("They are already here in the room with you.")));
        verify(targetLocation, never()).setRoom(eq(destination));
    }

    @Test
    void testTransferPlayer() {
        Output output = new Output();
        TransferCommand uut = new TransferCommand(repositoryBundle, commService, applicationContext, sessionAttributeService);
        Question result = uut.execute(question, webSocketContext, List.of(commandBinding, targetBinding), output);

        assertEquals(question, result);
        verify(targetLocation).setRoom(eq(destination));
    }
}
