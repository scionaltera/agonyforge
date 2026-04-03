package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.model.constant.WearSlot;
import com.agonyforge.mud.demo.model.impl.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InventoryCommandTest extends CommandTestBoilerplate {
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

    @Mock
    private Binding commandBinding;

    private final Random random = new Random();

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
        when(item.getLocation().getWorn()).thenReturn(EnumSet.noneOf(WearSlot.class));
        when(item.getItem()).thenReturn(itemComponent);
        when(item.getItem().getShortDescription()).thenReturn(itemName);
        when(armor.getLocation()).thenReturn(armorLocationComponent);
        when(armor.getLocation().getWorn()).thenReturn(EnumSet.of(WearSlot.BODY));
        when(itemRepository.findByLocationHeld(eq(ch))).thenReturn(List.of(armor, item));

        Output output = new Output();
        InventoryCommand uut = new InventoryCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of(commandBinding),
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
            List.of(commandBinding),
            output);

        verify(itemRepository).findByLocationHeld(eq(ch));

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("You are carrying:"));
        assertTrue(output.getOutput().get(1).contains("Nothing"));
    }
}
