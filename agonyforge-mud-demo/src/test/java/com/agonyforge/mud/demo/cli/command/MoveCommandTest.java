package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.service.SessionAttributeService;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.constant.Direction;
import com.agonyforge.mud.demo.model.impl.CharacterComponent;
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

import java.util.*;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MoveCommandTest {
    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private SessionAttributeService sessionAttributeService;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private MudItemRepository itemRepository;

    @Mock
    private MudRoomRepository roomRepository;

    @Mock
    private CommService commService;

    @Mock
    private WebSocketContext webSocketContext;

    @Mock
    private Question question;

    @Mock
    private MudCharacter ch;

    @Mock
    private CharacterComponent characterComponent;

    @Mock
    private MudRoom room;

    @Mock
    private MudRoom destination;

    private final Random random = new Random();

    @BeforeEach
    void setUp() {
        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        lenient().when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
        lenient().when(repositoryBundle.getRoomRepository()).thenReturn(roomRepository);
    }

    @Test
    void testExecute() {
        Long chId = random.nextLong();

        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(ch.getCharacter()).thenReturn(characterComponent);
        when(ch.getRoomId()).thenReturn(100L);
        when(roomRepository.findById(eq(100L))).thenReturn(Optional.of(room));
        when(roomRepository.findById(eq(101L))).thenReturn(Optional.of(destination));
        when(room.getExit(eq(Direction.WEST.getName()))).thenReturn(new MudRoom.Exit(101L));

        Input input = new Input("west");
        Output output = new Output();
        MoveCommand uut = new MoveCommand(repositoryBundle, commService, sessionAttributeService, applicationContext, Direction.WEST);
        Question response = uut.execute(question, webSocketContext, List.of("WEST"), input, output);

        assertEquals(question, response);

        verify(roomRepository).findById(eq(100L));
        verify(commService, times(2)).sendToRoom(eq(webSocketContext), anyLong(), any(Output.class));
        verify(ch).setRoomId(eq(101L));
        verify(characterRepository).save(any(MudCharacter.class));
    }

    @Test
    void testExecuteNoRoom() {
        Long chId = random.nextLong();

        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(ch.getCharacter()).thenReturn(characterComponent);
        when(ch.getRoomId()).thenReturn(100L);

        Input input = new Input("west");
        Output output = new Output();
        MoveCommand uut = new MoveCommand(repositoryBundle, commService, sessionAttributeService, applicationContext, Direction.WEST);
        Question response = uut.execute(question, webSocketContext, List.of("WEST"), input, output);

        assertEquals(1, output.getOutput().size());
        assertTrue(output.getOutput().get(0).contains("floating in the void"));

        assertEquals(question, response);

        verifyNoInteractions(commService);
        verify(roomRepository).findById(eq(100L));
        verify(characterRepository, never()).save(any(MudCharacter.class));
    }

    @Test
    void testExecuteNoExit() {
        Long chId = random.nextLong();

        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(ch.getRoomId()).thenReturn(100L);
        when(roomRepository.findById(eq(100L))).thenReturn(Optional.of(room));

        Input input = new Input("west");
        Output output = new Output();
        MoveCommand uut = new MoveCommand(repositoryBundle, commService, sessionAttributeService, applicationContext, Direction.WEST);
        Question response = uut.execute(question, webSocketContext, List.of("WEST"), input, output);

        assertEquals(1, output.getOutput().size());
        assertTrue(output.getOutput().get(0).contains("Alas, you cannot go that way."));

        assertEquals(question, response);

        verifyNoInteractions(commService);
        verify(roomRepository).findById(eq(100L));
        verify(characterRepository, never()).save(any(MudCharacter.class));
    }

    @Test
    void testExecuteBrokenExit() {
        Long chId = random.nextLong();

        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(ch.getRoomId()).thenReturn(100L);
        when(roomRepository.findById(eq(100L))).thenReturn(Optional.of(room));
        when(room.getExit(eq(Direction.WEST.getName()))).thenReturn(new MudRoom.Exit(101L));

        Input input = new Input("west");
        Output output = new Output();
        MoveCommand uut = new MoveCommand(repositoryBundle, commService, sessionAttributeService, applicationContext, Direction.WEST);
        Question response = uut.execute(question, webSocketContext, List.of("WEST"), input, output);

        assertEquals(1, output.getOutput().size());
        assertTrue(output.getOutput().get(0).contains("Alas, you cannot go that way."));

        assertEquals(question, response);

        verifyNoInteractions(commService);
        verify(roomRepository).findById(eq(100L));
        verify(roomRepository).findById(eq(101L));
        verify(characterRepository, never()).save(any(MudCharacter.class));
    }
}
