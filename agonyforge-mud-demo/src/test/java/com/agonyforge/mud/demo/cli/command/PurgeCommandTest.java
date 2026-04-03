package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.cli.TokenType;
import com.agonyforge.mud.demo.model.constant.Pronoun;
import com.agonyforge.mud.demo.model.impl.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PurgeCommandTest extends CommandTestBoilerplate {
    @Mock
    private Question question;

    @Mock
    private MudCharacter ch, target;

    @Mock
    private MudItem item;

    @Mock
    private MudRoom room;

    @Mock
    private LocationComponent chLocationComponent;

    @Mock
    private ItemComponent itemComponent;

    @Mock
    private CharacterComponent characterComponent, targetCharacterComponent;

    @Mock
    private Binding commandBinding, itemBinding, characterBinding;

    @BeforeEach
    void setUp() {
        when(webSocketContext.getAttributes()).thenReturn(Map.of(MUD_CHARACTER, 1L));
        when(characterRepository.findById(eq(1L))).thenReturn(Optional.of(ch));
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);
        when(ch.getCharacter()).thenReturn(characterComponent);
        lenient().when(ch.getCharacter().getPronoun()).thenReturn(Pronoun.SHE);
        lenient().when(characterRepository.findByLocationRoom(eq(room))).thenReturn(List.of(ch, target));
        lenient().when(target.getCharacter()).thenReturn(targetCharacterComponent);
        lenient().when(target.getCharacter().getName()).thenReturn("Greedo");
    }

    @Test
    void testPurgeInventoryItem() {
        when(item.getItem()).thenReturn(itemComponent);
        when(itemBinding.getType()).thenReturn(TokenType.ITEM_HELD);
        when(itemBinding.asItem()).thenReturn(item);

        Output output = new Output();
        PurgeCommand uut = new PurgeCommand(repositoryBundle, commService, applicationContext);

        Question result = uut.execute(question, webSocketContext, List.of(commandBinding, itemBinding), output);

        assertEquals(question, result);

        verify(itemRepository).delete(eq(item));
        verify(commService).sendToRoom(anyLong(), any(Output.class), eq(ch));
    }

    @Test
    void testPurgeRoomItem() {
        when(item.getItem()).thenReturn(itemComponent);
        when(itemBinding.getType()).thenReturn(TokenType.ITEM_GROUND);
        when(itemBinding.asItem()).thenReturn(item);

        Output output = new Output();
        PurgeCommand uut = new PurgeCommand(repositoryBundle, commService, applicationContext);

        Question result = uut.execute(question, webSocketContext, List.of(commandBinding, itemBinding), output);

        assertEquals(question, result);

        verify(itemRepository).delete(eq(item));
        verify(commService).sendToRoom(anyLong(), any(Output.class), eq(ch));
    }

    @Test
    void testPurgeRoomCharacter() {
        when(target.getCharacter()).thenReturn(targetCharacterComponent);
        when(characterBinding.getType()).thenReturn(TokenType.NPC_IN_ROOM);
        when(characterBinding.asCharacter()).thenReturn(target);

        Output output = new Output();
        PurgeCommand uut = new PurgeCommand(repositoryBundle, commService, applicationContext);

        Question result = uut.execute(question, webSocketContext, List.of(commandBinding, characterBinding), output);

        assertEquals(question, result);

        verify(characterRepository).delete(eq(target));
        verify(commService).sendToRoom(anyLong(), any(Output.class), eq(ch));
    }

    @Test
    void testPurgeRoomPlayer() {
        when(characterBinding.getType()).thenReturn(TokenType.CHARACTER_IN_ROOM);

        Output output = new Output();
        PurgeCommand uut = new PurgeCommand(repositoryBundle, commService, applicationContext);

        Question result = uut.execute(question, webSocketContext, List.of(commandBinding, characterBinding), output);

        assertEquals(question, result);

        verify(characterRepository, never()).delete(eq(target));
        verify(commService, never()).sendToRoom(anyLong(), any(Output.class), eq(ch));
    }
}
