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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InventoryCommandTest {
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
    private MudItemTemplate itemProto;

    @Mock
    private MudItem item;

    @Mock
    private ItemComponent itemComponent;

    @Mock
    private MudItem armor;

    @Mock
    private MudRoom room;

    @Mock
    private LocationComponent itemLocationComponent, armorLocationComponent, chLocationComponent;

    private final Random random = new Random();

    @BeforeEach
    void setUp() {
        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        lenient().when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
        lenient().when(repositoryBundle.getRoomRepository()).thenReturn(roomRepository);
    }

    @Test
    void testInventory() {
        Long chId = random.nextLong();
        String itemName = "a scurrilous test";

        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);
        when(itemProto.getId()).thenReturn(102L);
        when(item.getTemplate()).thenReturn(itemProto);
        when(item.getLocation()).thenReturn(itemLocationComponent);
        when(item.getItem()).thenReturn(itemComponent);
        when(item.getItem().getShortDescription()).thenReturn(itemName);
        when(armor.getLocation()).thenReturn(armorLocationComponent);
        when(armor.getLocation().getWorn()).thenReturn(WearSlot.BODY);
        when(itemRepository.findByLocationHeld(eq(ch))).thenReturn(List.of(armor, item));

        Output output = new Output();
        InventoryCommand uut = new InventoryCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("INVENTORY"),
            new Input("i"),
            output);

        verify(itemRepository).findByLocationHeld(eq(ch));

        assertEquals(question, result);
        assertEquals(2, output.getOutput().size());
        assertTrue(output.getOutput().get(0).contains("You are carrying:"));
        assertTrue(output.getOutput().get(1).contains(itemName));
    }

    @Test
    void testInventoryEmpty() {
        Long chId = random.nextLong();

        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);

        Output output = new Output();
        InventoryCommand uut = new InventoryCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("INVENTORY"),
            new Input("i"),
            output);

        verify(itemRepository).findByLocationHeld(eq(ch));

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("You are carrying:"));
        assertTrue(output.getOutput().get(1).contains("Nothing"));
    }
}
