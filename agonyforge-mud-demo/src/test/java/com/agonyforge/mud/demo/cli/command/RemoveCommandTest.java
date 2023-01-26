package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.impl.MudItem;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudItemRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudRoomRepository;
import com.agonyforge.mud.models.dynamodb.service.CommService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RemoveCommandTest {
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
    private MudItem target;

    @Test
    void testRemoveNoArg() {
        UUID chId = UUID.randomUUID();
        when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));

        Output output = new Output();
        RemoveCommand uut = new RemoveCommand(characterRepository, itemRepository, roomRepository, commService);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("REMOVE"),
            new Input("rem"),
            output);

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("What would you like to remove?"));
    }

    @Test
    void testRemoveNoTarget() {
        UUID chId = UUID.randomUUID();
        when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));

        Output output = new Output();
        RemoveCommand uut = new RemoveCommand(characterRepository, itemRepository, roomRepository, commService);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("REMOVE", "HAT"),
            new Input("rem hat"),
            output);

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("You aren't wearing anything like that"));
    }

    @Test
    void testRemoveTargetNotWorn() {
        UUID chId = UUID.randomUUID();
        when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(itemRepository.getByCharacter(eq(ch.getId()))).thenReturn(List.of(target));
        when(target.getNameList()).thenReturn(List.of("test", "hat"));
        when(target.getShortDescription()).thenReturn("a test hat");

        Output output = new Output();
        RemoveCommand uut = new RemoveCommand(characterRepository, itemRepository, roomRepository, commService);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("REMOVE", "HAT"),
            new Input("rem hat"),
            output);

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("You aren't wearing a test hat"));
    }

    @Test
    void testRemoveTarget() {
        UUID chId = UUID.randomUUID();
        when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(itemRepository.getByCharacter(eq(ch.getId()))).thenReturn(List.of(target));
        when(target.getNameList()).thenReturn(List.of("test", "hat"));
        when(target.getShortDescription()).thenReturn("a test hat");
        when(target.getWorn()).thenReturn("head");

        Output output = new Output();
        RemoveCommand uut = new RemoveCommand(characterRepository, itemRepository, roomRepository, commService);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("REMOVE", "HAT"),
            new Input("rem hat"),
            output);

        verify(target).setWorn(eq(null));
        verify(itemRepository).save(any(MudItem.class));
        verify(commService).sendToRoom(
            eq(webSocketContext),
            anyLong(),
            any(Output.class));

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("You remove a test hat"));
    }
}
