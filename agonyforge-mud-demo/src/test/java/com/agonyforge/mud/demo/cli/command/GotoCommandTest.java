package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.service.SessionAttributeService;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.CharacterComponent;
import com.agonyforge.mud.demo.model.impl.LocationComponent;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudRoom;
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
public class GotoCommandTest {
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
    private MudRoom room, destination;

    @Mock
    private Binding commandBinding, targetBinding, roomBinding;

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
        lenient().when(chLocation.getRoom()).thenReturn(room);
        lenient().when(ch.getCharacter()).thenReturn(chCharacter);
        lenient().when(chCharacter.getName()).thenReturn("Scion");

        lenient().when(mudCharacterRepository.findById(eq(targetId))).thenReturn(Optional.of(target));
        lenient().when(target.getLocation()).thenReturn(targetLocation);
        lenient().when(targetLocation.getRoom()).thenReturn(destination);
        lenient().when(target.getCharacter()).thenReturn(targetCharacter);
        lenient().when(targetCharacter.getName()).thenReturn("Target");
    }

    @Test
    void testGotoPlayer() {
        when(mudCharacterRepository.findAll()).thenReturn(List.of(ch, target));
        when(targetBinding.asCharacter()).thenReturn(target);

        Output output = new Output();
        GotoCommand uut = new GotoCommand(repositoryBundle, commService, applicationContext, sessionAttributeService);
        Question result = uut.execute(question, webSocketContext, List.of(commandBinding, targetBinding), output);

        assertEquals(question, result);
        verify(chLocation).setRoom(eq(destination));
    }

    @Test
    void testGotoRoom() {
        when(roomBinding.asRoom()).thenReturn(destination);

        Output output = new Output();
        GotoCommand uut = new GotoCommand(repositoryBundle, commService, applicationContext, sessionAttributeService);
        Question result = uut.execute(question, webSocketContext, List.of(commandBinding, roomBinding), output);

        assertEquals(question, result);
        verify(chLocation).setRoom(eq(destination));
    }
}
