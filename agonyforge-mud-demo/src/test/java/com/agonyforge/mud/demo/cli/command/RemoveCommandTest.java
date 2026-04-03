package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.model.constant.WearSlot;
import com.agonyforge.mud.demo.model.impl.*;
import com.agonyforge.mud.demo.model.constant.Pronoun;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RemoveCommandTest extends CommandTestBoilerplate {
    @Mock
    private CharacterComponent characterComponent;

    @Mock
    private MudItem target;

    @Mock
    private ItemComponent itemComponent;

    @Mock
    private LocationComponent chLocationComponent, targetLocationComponent;

    @Mock
    private MudRoom room;

    @Mock
    private Binding itemBinding;

    @BeforeEach
    void setUp() {
        when(itemBinding.asItem()).thenReturn(target);
    }

    @Test
    void testRemoveTarget() {
        when(characterComponent.getPronoun()).thenReturn(Pronoun.SHE);
        when(room.getId()).thenReturn(100L);
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);
        when(ch.getCharacter()).thenReturn(characterComponent);
        when(target.getItem()).thenReturn(itemComponent);
        when(target.getItem().getShortDescription()).thenReturn("a test hat");
        when(target.getLocation()).thenReturn(targetLocationComponent);
        when(target.getLocation().getWorn()).thenReturn(EnumSet.of(WearSlot.HEAD));

        Output output = new Output();
        RemoveCommand uut = new RemoveCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of(commandBinding, itemBinding),
            output);

        verify(targetLocationComponent).setHeld(eq(ch));
        verify(targetLocationComponent).setRoom(eq(null));
        verify(targetLocationComponent).setWorn(eq(EnumSet.noneOf(WearSlot.class)));
        verify(itemRepository).save(any(MudItem.class));
        verify(commService).sendToRoom(
            anyLong(),
            any(Output.class),
            eq(ch));

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("You remove a test hat"));
    }
}
