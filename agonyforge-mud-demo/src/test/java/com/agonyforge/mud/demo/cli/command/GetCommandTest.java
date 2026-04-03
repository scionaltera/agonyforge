package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.model.constant.WearSlot;
import com.agonyforge.mud.demo.model.impl.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetCommandTest extends CommandTestBoilerplate {
    @Mock
    private MudRoom room;

    @Mock
    private CharacterComponent characterComponent;

    @Mock
    private MudItem item;

    @Mock
    private MudItem other;

    @Mock
    private ItemComponent itemComponent;

    @Mock
    private LocationComponent itemLocationComponent, chLocationComponent;

    @Mock
    private Binding itemBinding;

    @BeforeEach
    void setUp() {
        when(itemBinding.asItem()).thenReturn(item);
    }

    @Test
    void testGet() {
        Long roomId = 100L;
        String itemName = "a scurrilous test";

        when(room.getId()).thenReturn(roomId);
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);
        when(ch.getCharacter()).thenReturn(characterComponent);
        when(item.getLocation()).thenReturn(itemLocationComponent);
        when(item.getItem()).thenReturn(itemComponent);
        when(item.getItem().getShortDescription()).thenReturn(itemName);

        Output output = new Output();
        GetCommand uut = new GetCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of(commandBinding, itemBinding),
            output);

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
