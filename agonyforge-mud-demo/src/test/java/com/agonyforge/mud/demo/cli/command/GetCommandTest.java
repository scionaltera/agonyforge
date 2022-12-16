package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.impl.MudItem;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudItemRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetCommandTest {
    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private MudItemRepository itemRepository;

    @Mock
    private CommService commService;

    @Mock
    private WebSocketContext webSocketContext;

    @Mock
    private Question question;

    @Mock
    private MudCharacter ch;

    @Mock
    private MudItem item;

    @Mock
    private MudItem other;

    @Test
    void testGetNoCharacter() {
        UUID chId = UUID.randomUUID();

        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));

        Output output = new Output();
        GetCommand uut = new GetCommand(characterRepository, itemRepository, commService);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("GET", "TEST"),
            new Input("g t"),
            output);

        verifyNoInteractions(itemRepository);

        assertEquals(question, result);
    }

    @Test
    void testGetCharacterInVoid() {
        UUID chId = UUID.randomUUID();

        when(ch.getRoomId()).thenReturn(null);
        when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));

        Output output = new Output();
        GetCommand uut = new GetCommand(characterRepository, itemRepository, commService);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("GET", "TEST"),
            new Input("g t"),
            output);

        verifyNoInteractions(itemRepository);

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("nothing to get here"));
    }

    @Test
    void testGetNoItem() {
        UUID chId = UUID.randomUUID();
        Long roomId = 100L;

        when(ch.getRoomId()).thenReturn(roomId);
        when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(itemRepository.getByRoom(eq(roomId))).thenReturn(List.of(other));

        Output output = new Output();
        GetCommand uut = new GetCommand(characterRepository, itemRepository, commService);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("GET", "TEST"),
            new Input("g t"),
            output);

        verify(itemRepository).getByRoom(eq(roomId));

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("anything like that here"));
    }

    @Test
    void testGet() {
        UUID chId = UUID.randomUUID();
        Long roomId = 100L;
        String itemName = "a scurrilous test";

        when(ch.getId()).thenReturn(chId);
        when(ch.getRoomId()).thenReturn(roomId);
        when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(item.getNameList()).thenReturn(List.of("test"));
        when(item.getShortDescription()).thenReturn(itemName);
        when(itemRepository.getByRoom(eq(roomId))).thenReturn(List.of(other, item));

        Output output = new Output();
        GetCommand uut = new GetCommand(characterRepository, itemRepository, commService);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("GET", "TEST"),
            new Input("g t"),
            output);

        verify(itemRepository).getByRoom(eq(roomId));
        verify(item).setCharacterId(eq(chId));
        verify(itemRepository).save(eq(item));
        verify(itemRepository, never()).save(eq(other));
        verify(commService).sendToRoom(eq(webSocketContext), eq(roomId), any(Output.class));

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("You get " + itemName));
    }
}
