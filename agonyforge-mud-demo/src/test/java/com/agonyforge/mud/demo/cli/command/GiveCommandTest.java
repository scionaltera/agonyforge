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

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GiveCommandTest extends CommandTestBoilerplate {
    @Mock
    private Question question;

    @Mock
    private MudRoom room;

    @Mock
    private MudCharacter ch;

    @Mock
    private MudCharacter target;

    @Mock
    private CharacterComponent chCharacterComponent, targetCharacterComponent;

    @Mock
    private MudItem item;

    @Mock
    private ItemComponent itemComponent;

    @Mock
    private LocationComponent itemLocationComponent, chLocationComponent;

    @Mock
    private Binding commandBinding, itemBinding, targetBinding;

    private final Random random = new Random();

    @BeforeEach
    void setUp() {
        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        lenient().when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
        lenient().when(repositoryBundle.getRoomRepository()).thenReturn(roomRepository);
    }

    @Test
    void testGive() {
        Long chId = random.nextLong();
        Long roomId = 100L;

        when(room.getId()).thenReturn(roomId);
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);
        when(ch.getCharacter()).thenReturn(chCharacterComponent);
        when(chCharacterComponent.getName()).thenReturn("Scion");
        when(target.getCharacter()).thenReturn(targetCharacterComponent);
        when(targetCharacterComponent.getName()).thenReturn("Spook");
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(item.getLocation()).thenReturn(itemLocationComponent);
        when(item.getItem()).thenReturn(itemComponent);
        when(item.getItem().getShortDescription()).thenReturn("a spoon");
        when(itemBinding.asItem()).thenReturn(item);
        when(targetBinding.asCharacter()).thenReturn(target);

        Output output = new Output();
        GiveCommand uut = new GiveCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of(commandBinding, itemBinding, targetBinding),
            output);

        verify(itemLocationComponent).setHeld(eq(target));
        verify(itemLocationComponent).setWorn(eq(EnumSet.noneOf(WearSlot.class)));
        verify(itemLocationComponent).setRoom(eq(null));
        verify(itemRepository).save(eq(item));
        verify(commService).sendTo(eq(target), any(Output.class));
        verify(commService).sendToRoom(eq(roomId), any(Output.class), eq(ch), eq(target));

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("You give a spoon[default] to Spook."));
    }
}
