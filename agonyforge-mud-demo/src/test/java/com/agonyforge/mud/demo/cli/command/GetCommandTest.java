package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.constant.WearSlot;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetCommandTest {
    @Mock
    private RepositoryBundle repositoryBundle;

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
    private MudRoom room;

    @Mock
    private MudCharacter ch;

    @Mock
    private CharacterComponent characterComponent;

    @Mock
    private MudItem item;

    @Mock
    private MudItem other;

    @Mock
    private ItemComponent itemComponent, otherItemComponent;

    @Mock
    private LocationComponent itemLocationComponent, chLocationComponent;

    private final Random random = new Random();

    @BeforeEach
    void setUp() {
        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        lenient().when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
        lenient().when(repositoryBundle.getRoomRepository()).thenReturn(roomRepository);
    }

    @Test
    void testGetNoArg() {
        Long chId = random.nextLong();

        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));

        Output output = new Output();
        GetCommand uut = new GetCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("GET"),
            new Input("g"),
            output);

        verify(itemRepository, never()).findByLocationRoom(eq(room));
        verify(itemRepository, never()).save(any(MudItem.class));

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("What would you like to get"));
    }

    @Test
    void testGetNoItem() {
        Long chId = random.nextLong();
        Long roomId = 100L;

        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(other.getItem()).thenReturn(otherItemComponent);
        when(other.getItem().getNameList()).thenReturn(Set.of("sword"));
        when(itemRepository.findByLocationRoom(eq(room))).thenReturn(List.of(other));

        Output output = new Output();
        GetCommand uut = new GetCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("GET", "TEST"),
            new Input("g t"),
            output);

        verify(itemRepository).findByLocationRoom(eq(room));
        verify(itemRepository, never()).save(any(MudItem.class));

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("anything like that here"));
    }

    @Test
    void testGet() {
        Long chId = random.nextLong();
        Long roomId = 100L;
        String itemName = "a scurrilous test";

        when(room.getId()).thenReturn(roomId);
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);
        when(ch.getCharacter()).thenReturn(characterComponent);
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(item.getLocation()).thenReturn(itemLocationComponent);
        when(item.getItem()).thenReturn(itemComponent);
        when(item.getItem().getNameList()).thenReturn(Set.of("test"));
        when(item.getItem().getShortDescription()).thenReturn(itemName);
        when(other.getItem()).thenReturn(otherItemComponent);
        when(other.getItem().getNameList()).thenReturn(Set.of("sword"));
        when(itemRepository.findByLocationRoom(eq(room))).thenReturn(List.of(other, item));

        Output output = new Output();
        GetCommand uut = new GetCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("GET", "TEST"),
            new Input("g t"),
            output);

        verify(itemRepository).findByLocationRoom(eq(room));
        verify(itemLocationComponent).setHeld(eq(ch));
        verify(itemLocationComponent).setRoom(eq(null));
        verify(itemLocationComponent).setWorn(eq(EnumSet.noneOf(WearSlot.class)));
        verify(itemRepository).save(eq(item));
        verify(itemRepository, never()).save(eq(other));
        verify(commService).sendToRoom(eq(roomId), any(Output.class), eq(ch));

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("You get " + itemName));
    }
}
