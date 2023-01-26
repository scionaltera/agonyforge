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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WearCommandTest {
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
    private MudItem target;

    @Test
    void testWearNoArg() {
        UUID chId = UUID.randomUUID();
        when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));

        Output output = new Output();
        WearCommand uut = new WearCommand(characterRepository, itemRepository, roomRepository, commService);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("WEAR"),
            new Input("wea"),
            output);

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("What would you like to wear?"));
    }

    @Test
    void testWearNoInventoryItem() {
        UUID chId = UUID.randomUUID();
        when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));

        Output output = new Output();
        WearCommand uut = new WearCommand(characterRepository, itemRepository, roomRepository, commService);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("WEAR", "HAT"),
            new Input("wea ha"),
            output);

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("You aren't carrying anything like that."));
    }

    @Test
    void testWearTargetAlreadyWorn() {
        UUID chId = UUID.randomUUID();
        when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(itemRepository.getByCharacter(ch.getId())).thenReturn(List.of(target));
        when(target.getShortDescription()).thenReturn("a test hat");
        when(target.getNameList()).thenReturn(List.of("hat"));
        when(target.getWorn()).thenReturn("head");

        Output output = new Output();
        WearCommand uut = new WearCommand(characterRepository, itemRepository, roomRepository, commService);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("WEAR", "HAT"),
            new Input("wea ha"),
            output);

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("You are already wearing a test hat"));
    }

    @Test
    void testWearTargetNoAvailableSlot() {
        UUID chId = UUID.randomUUID();
        when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(itemRepository.getByCharacter(ch.getId())).thenReturn(List.of(item, target));
        when(item.getNameList()).thenReturn(List.of("rubber", "chicken"));
        when(item.getWorn()).thenReturn("head");
        when(target.getNameList()).thenReturn(List.of("hat"));

        Output output = new Output();
        WearCommand uut = new WearCommand(characterRepository, itemRepository, roomRepository, commService);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("WEAR", "HAT"),
            new Input("wea ha"),
            output);

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("You need to take something else off"));
    }

    @Test
    void testWearTarget() {
        UUID chId = UUID.randomUUID();
        when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(itemRepository.getByCharacter(ch.getId())).thenReturn(List.of(target));
        when(ch.getWearSlots()).thenReturn(List.of("head"));
        when(target.getShortDescription()).thenReturn("a test hat");
        when(target.getNameList()).thenReturn(List.of("hat"));
        when(target.getWearSlots()).thenReturn(List.of("head"));

        Output output = new Output();
        WearCommand uut = new WearCommand(characterRepository, itemRepository, roomRepository, commService);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("WEAR", "HAT"),
            new Input("wea ha"),
            output);

        verify(target).setWorn(anyString());
        verify(itemRepository).save(any(MudItem.class));
        verify(commService).sendToRoom(
            eq(webSocketContext),
            anyLong(),
            any(Output.class));

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("You wear a test hat[default] on your head"));
    }

    @Test
    void testWearTargetSecondMatch() {
        UUID chId = UUID.randomUUID();
        when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(itemRepository.getByCharacter(ch.getId())).thenReturn(List.of(item, target));
        when(ch.getWearSlots()).thenReturn(List.of("hand", "head"));
        when(item.getNameList()).thenReturn(List.of("rubber", "chicken"));
        when(item.getWorn()).thenReturn("hand");
        when(target.getShortDescription()).thenReturn("a test hat");
        when(target.getNameList()).thenReturn(List.of("hat"));
        when(target.getWearSlots()).thenReturn(List.of("hand", "head"));

        Output output = new Output();
        WearCommand uut = new WearCommand(characterRepository, itemRepository, roomRepository, commService);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("WEAR", "HAT"),
            new Input("wea ha"),
            output);

        verify(target).setWorn(eq("head"));
        verify(itemRepository).save(any(MudItem.class));
        verify(commService).sendToRoom(
            eq(webSocketContext),
            anyLong(),
            any(Output.class));

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("You wear a test hat[default] on your head"));
    }

    @Test
    void testWearTargetWithOtherItem() {
        UUID chId = UUID.randomUUID();
        when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(itemRepository.getByCharacter(ch.getId())).thenReturn(List.of(item, target));
        when(ch.getWearSlots()).thenReturn(List.of("hand", "head"));
        when(item.getNameList()).thenReturn(List.of("rubber", "chicken"));
        when(item.getWorn()).thenReturn("hand");
        when(target.getShortDescription()).thenReturn("a test hat");
        when(target.getNameList()).thenReturn(List.of("hat"));
        when(target.getWearSlots()).thenReturn(List.of("head"));

        Output output = new Output();
        WearCommand uut = new WearCommand(characterRepository, itemRepository, roomRepository, commService);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("WEAR", "HAT"),
            new Input("wea ha"),
            output);

        verify(target).setWorn(anyString());
        verify(itemRepository).save(any(MudItem.class));
        verify(commService).sendToRoom(
            eq(webSocketContext),
            anyLong(),
            any(Output.class));

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("You wear a test hat[default] on your head"));
    }
}
