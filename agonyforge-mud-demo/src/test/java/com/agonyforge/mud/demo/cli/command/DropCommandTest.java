package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.Binding;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DropCommandTest {
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
    private LocationComponent locationComponent, otherLocationComponent, chLocationComponent;

    @Mock
    private MudRoom room;

    @Mock
    private Binding commandBinding, itemBinding;

    private final Random random = new Random();

    @BeforeEach
    void setUp() {
        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        lenient().when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
        lenient().when(repositoryBundle.getRoomRepository()).thenReturn(roomRepository);

        when(itemBinding.asItem()).thenReturn(item);
    }

    @Test
    void testDrop() {
        Long chId = random.nextLong();
        String itemName = "a scurrilous test";

        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);
        when(ch.getCharacter()).thenReturn(characterComponent);
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(room.getId()).thenReturn(100L);
        when(item.getLocation()).thenReturn(locationComponent);
        when(item.getLocation().getWorn()).thenReturn(EnumSet.noneOf(WearSlot.class));
        when(item.getItem()).thenReturn(itemComponent);
        when(item.getItem().getNameList()).thenReturn(Set.of("test"));
        when(item.getItem().getShortDescription()).thenReturn(itemName);
        when(other.getLocation()).thenReturn(otherLocationComponent);
        when(other.getItem()).thenReturn(otherItemComponent);
        when(other.getItem().getNameList()).thenReturn(Set.of("sword"));
        when(itemRepository.findByLocationHeld(eq(ch))).thenReturn(List.of(other, item));

        Output output = new Output();
        DropCommand uut = new DropCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of(commandBinding, itemBinding),
            output);

        verify(itemRepository).findByLocationHeld(eq(ch));
        verify(locationComponent).setHeld(eq(null));
        verify(locationComponent).setRoom(eq(room));
        verify(locationComponent).setWorn(eq(EnumSet.noneOf(WearSlot.class)));
        verify(itemRepository).save(eq(item));
        verify(itemRepository, never()).save(eq(other));
        verify(commService).sendToRoom(eq(100L), any(Output.class), eq(ch));

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("You drop " + itemName));
    }
}
