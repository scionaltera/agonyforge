package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.service.SessionAttributeService;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.constant.AdminFlag;
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

import java.util.*;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeleportCommandTest {
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
        lenient().when(chPlayer.getAdminFlags()).thenReturn(EnumSet.noneOf(AdminFlag.class));

        lenient().when(mudCharacterRepository.findById(eq(targetId))).thenReturn(Optional.of(target));
        lenient().when(target.getLocation()).thenReturn(targetLocation);
        lenient().when(targetLocation.getRoom()).thenReturn(room);
        lenient().when(target.getCharacter()).thenReturn(targetCharacter);
        lenient().when(targetCharacter.getName()).thenReturn("Target");
        lenient().when(targetCharacter.getPronoun()).thenReturn(Pronoun.THEY);
        lenient().when(target.getPlayer()).thenReturn(targetPlayer);
        lenient().when(targetPlayer.getAdminFlags()).thenReturn(EnumSet.noneOf(AdminFlag.class));
    }

    @Test
    void testTeleportNoArgs() {
        Output output = new Output();
        TeleportCommand uut = new TeleportCommand(repositoryBundle, commService, applicationContext, sessionAttributeService);
        Question result = uut.execute(question, webSocketContext, List.of("teleport"), output);

        assertEquals(question, result);
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("Whom do you wish to teleport?")));
    }

    @Test
    void testTeleportOneArgs() {
        Output output = new Output();
        TeleportCommand uut = new TeleportCommand(repositoryBundle, commService, applicationContext, sessionAttributeService);
        Question result = uut.execute(question, webSocketContext, List.of("teleport", "carmen"), output);

        assertEquals(question, result);
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("Where would you like to send them?")));
    }

    @Test
    void testTeleportTooManyArgs() {
        Output output = new Output();
        TeleportCommand uut = new TeleportCommand(repositoryBundle, commService, applicationContext, sessionAttributeService);
        Question result = uut.execute(question, webSocketContext, List.of("teleport", "carmen", "sandiego", "now"), output);

        assertEquals(question, result);
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("TELEPORT &lt;victim&gt; &lt;destination&gt;")));
    }

    @Test
    void testTeleportPlayerNotFound() {
        when(mudCharacterRepository.findAll()).thenReturn(List.of(ch, target));

        Output output = new Output();
        TeleportCommand uut = new TeleportCommand(repositoryBundle, commService, applicationContext, sessionAttributeService);
        Question result = uut.execute(question, webSocketContext, List.of("teleport", "carmen", "3000"), output);

        assertEquals(question, result);
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("No such player exists.")));
        verify(chLocation, never()).setRoom(eq(destination));
    }

    @Test
    void testTeleportRoomNotFound() {
        when(mudCharacterRepository.findAll()).thenReturn(List.of(ch, target));

        Output output = new Output();
        TeleportCommand uut = new TeleportCommand(repositoryBundle, commService, applicationContext, sessionAttributeService);
        Question result = uut.execute(question, webSocketContext, List.of("teleport", "target", "9000"), output);

        assertEquals(question, result);
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("No such room exists.")));
        verify(chLocation, never()).setRoom(eq(destination));
    }

    @Test
    void testTeleportPlayer() {
        when(mudCharacterRepository.findAll()).thenReturn(List.of(ch, target));

        Output output = new Output();
        TeleportCommand uut = new TeleportCommand(repositoryBundle, commService, applicationContext, sessionAttributeService);
        Question result = uut.execute(question, webSocketContext, List.of("teleport", "target", "3000"), output);

        assertEquals(question, result);
        verify(targetLocation).setRoom(eq(destination));
    }
}
