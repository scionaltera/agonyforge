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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InventoryCommandTest {
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
    private MudItem item;

    @Test
    void testInventory() {
        UUID chId = UUID.randomUUID();
        String itemName = "a scurrilous test";

        when(ch.getId()).thenReturn(chId);
        when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(item.getShortDescription()).thenReturn(itemName);
        when(itemRepository.getByCharacter(eq(chId))).thenReturn(List.of(item));

        Output output = new Output();
        InventoryCommand uut = new InventoryCommand(characterRepository, itemRepository, roomRepository, commService);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("INVENTORY"),
            new Input("i"),
            output);

        verify(itemRepository).getByCharacter(eq(chId));

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("Your inventory"));
        assertTrue(output.getOutput().get(1).contains(itemName));
    }

    @Test
    void testInventoryEmpty() {
        UUID chId = UUID.randomUUID();

        when(ch.getId()).thenReturn(chId);
        when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));

        Output output = new Output();
        InventoryCommand uut = new InventoryCommand(characterRepository, itemRepository, roomRepository, commService);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("INVENTORY"),
            new Input("i"),
            output);

        verify(itemRepository).getByCharacter(eq(chId));

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("aren't carrying anything"));
    }
}
