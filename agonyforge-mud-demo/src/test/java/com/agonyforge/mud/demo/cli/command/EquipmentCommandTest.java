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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EquipmentCommandTest extends CommandTestBoilerplate {
    @Mock
    private MudRoom room;

    @Mock
    private Question question;

    @Mock
    private MudCharacter ch;

    @Mock
    private MudItem item;

    @Mock
    private MudItem junk;

    @Mock
    private ItemComponent itemComponent;

    @Mock
    private LocationComponent itemLocationComponent, chLocationComponent;

    @Mock
    private Binding commandBinding;

    private final Random random = new Random();

    @Test
    void testEquipmentNone() {
        Long chId = random.nextLong();

        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));

        Output output = new Output();
        EquipmentCommand uut = new EquipmentCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of(commandBinding),
            output);

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("You are using"));
        assertTrue(output.getOutput().get(1).contains("Nothing."));
    }

    @Test
    void testEquipment() {
        Long chId = random.nextLong();

        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(itemRepository.findByLocationHeld(eq(ch))).thenReturn(List.of(junk, item));
        when(item.getLocation()).thenReturn(itemLocationComponent);
        when(item.getLocation().getWorn()).thenReturn(EnumSet.of(WearSlot.HEAD));
        when(item.getItem()).thenReturn(itemComponent);
        when(item.getItem().getShortDescription()).thenReturn("a rubber chicken");

        Output output = new Output();
        EquipmentCommand uut = new EquipmentCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of(commandBinding),
            output);

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("You are using"));
        assertTrue(output.getOutput().get(1).contains("&lt;worn on head&gt;\ta rubber chicken"));
    }
}
