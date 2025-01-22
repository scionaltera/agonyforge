package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.service.SessionAttributeService;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.constant.Pronoun;
import com.agonyforge.mud.demo.model.impl.*;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import com.agonyforge.mud.demo.model.repository.MudItemRepository;
import com.agonyforge.mud.demo.model.repository.MudRoomRepository;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransferCommandTest {
    private static final Random RANDOM = new Random();

    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private CommService commService;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private SessionAttributeService sessionAttributeService;

    @Mock
    private Question question;

    @Mock
    private WebSocketContext webSocketContext;

    @Mock
    private MudItemRepository mudItemRepository;

    @Mock
    private MudRoomRepository mudRoomRepository;

    @Mock
    private MudCharacterRepository mudCharacterRepository;

    @Mock
    private MudCharacter ch, target;

    @Mock
    private LocationComponent chLocation, targetLocation;

    @Mock
    private CharacterComponent chCharacter, targetCharacter;

    @Mock
    private PlayerComponent chPlayer, targetPlayer;

    @Mock
    private MudRoom room, destination;

    @BeforeEach
    void setUp() {
        Long chId = RANDOM.nextLong();
        Long targetId = RANDOM.nextLong();
        Long destinationId = 3000L;

        lenient().when(webSocketContext.getAttributes()).thenReturn(Map.of(MUD_CHARACTER, chId));

        lenient().when(repositoryBundle.getItemRepository()).thenReturn(mudItemRepository);
        lenient().when(repositoryBundle.getRoomRepository()).thenReturn(mudRoomRepository);

        lenient().when(mudRoomRepository.findById(destinationId)).thenReturn(Optional.of(destination));

        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(mudCharacterRepository);
        lenient().when(mudCharacterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        lenient().when(ch.getLocation()).thenReturn(chLocation);
        lenient().when(chLocation.getRoom()).thenReturn(destination);
        lenient().when(ch.getCharacter()).thenReturn(chCharacter);
        lenient().when(chCharacter.getName()).thenReturn("Scion");
        lenient().when(ch.getPlayer()).thenReturn(chPlayer);

        lenient().when(mudCharacterRepository.findById(eq(targetId))).thenReturn(Optional.of(target));
        lenient().when(target.getLocation()).thenReturn(targetLocation);
        lenient().when(targetLocation.getRoom()).thenReturn(room);
        lenient().when(target.getCharacter()).thenReturn(targetCharacter);
        lenient().when(targetCharacter.getName()).thenReturn("Target");
        lenient().when(targetCharacter.getPronoun()).thenReturn(Pronoun.THEY);
        lenient().when(target.getPlayer()).thenReturn(chPlayer);
    }

    @Test
    void testTransferNoArgs() {
        Output output = new Output();

        TransferCommand uut = new TransferCommand(repositoryBundle, commService, applicationContext, sessionAttributeService);

        Question result = uut.execute(question, webSocketContext, List.of("TRANSFER"), new Input("transfer"), output);

        assertEquals(question, result);
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("Whom would you like to transfer?")));
    }

    @Test
    void testTransferTooManyArgs() {
        Output output = new Output();

        TransferCommand uut = new TransferCommand(repositoryBundle, commService, applicationContext, sessionAttributeService);

        Question result = uut.execute(question, webSocketContext, List.of("TRANSFER", "CARMEN", "SANDIEGO", "NOW"), new Input("transfer carmen sandiego now"), output);

        assertEquals(question, result);
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("Whom would you like to transfer?")));
    }

    @Test
    void testTransferPlayerNotFound() {
        when(mudCharacterRepository.findAll()).thenReturn(List.of(ch, target));

        Output output = new Output();
        TransferCommand uut = new TransferCommand(repositoryBundle, commService, applicationContext, sessionAttributeService);
        Question result = uut.execute(question, webSocketContext, List.of("TRANSFER", "CARMEN"), new Input("transfer carmen"), output);

        assertEquals(question, result);
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("Can't find that player.")));
        verify(chLocation, never()).setRoom(eq(destination));
    }

    @Test
    void testTransferPlayerSameRoom() {
        when(mudCharacterRepository.findAll()).thenReturn(List.of(ch, target));
        lenient().when(targetLocation.getRoom()).thenReturn(destination);

        Output output = new Output();
        TransferCommand uut = new TransferCommand(repositoryBundle, commService, applicationContext, sessionAttributeService);
        Question result = uut.execute(question, webSocketContext, List.of("TRANSFER", "TARGET"), new Input("transfer target"), output);

        assertEquals(question, result);
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("They are already here in the room with you.")));
        verify(targetLocation, never()).setRoom(eq(destination));
    }

    @Test
    void testTransferPlayer() {
        when(mudCharacterRepository.findAll()).thenReturn(List.of(ch, target));

        Output output = new Output();
        TransferCommand uut = new TransferCommand(repositoryBundle, commService, applicationContext, sessionAttributeService);
        Question result = uut.execute(question, webSocketContext, List.of("TRANSFER", "TARGET"), new Input("transfer target"), output);

        assertEquals(question, result);
        verify(targetLocation).setRoom(eq(destination));
    }
}
