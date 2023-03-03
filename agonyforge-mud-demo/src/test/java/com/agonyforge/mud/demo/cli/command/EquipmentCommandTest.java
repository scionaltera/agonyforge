package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.models.dynamodb.constant.WearSlot;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EquipmentCommandTest {
    @Mock
    private RepositoryBundle repositoryBundle;

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

    @Mock
    private MudItem junk;

    @Test
    void testEquipmentNone() {
        UUID chId = UUID.randomUUID();

        when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
        when(repositoryBundle.getRoomRepository()).thenReturn(roomRepository);
        when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));

        Output output = new Output();
        EquipmentCommand uut = new EquipmentCommand(repositoryBundle, commService);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("EQUIPMENT"),
            new Input("eq"),
            output);

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("You are using"));
        assertTrue(output.getOutput().get(1).contains("Nothing."));
    }

    @Test
    void testEquipment() {
        UUID chId = UUID.randomUUID();

        when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
        when(repositoryBundle.getRoomRepository()).thenReturn(roomRepository);
        when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(itemRepository.getByCharacter(eq(ch.getId()))).thenReturn(List.of(junk, item));
        when(item.getWorn()).thenReturn(WearSlot.HEAD);
        when(item.getShortDescription()).thenReturn("a rubber chicken");

        Output output = new Output();
        EquipmentCommand uut = new EquipmentCommand(repositoryBundle, commService);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("EQUIPMENT"),
            new Input("eq"),
            output);

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("You are using"));
        assertTrue(output.getOutput().get(1).contains("&lt;worn on head>\ta rubber chicken"));
    }
}
