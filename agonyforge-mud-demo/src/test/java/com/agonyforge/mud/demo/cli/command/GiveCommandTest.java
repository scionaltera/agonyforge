package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.constant.WearSlot;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudItem;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import com.agonyforge.mud.demo.model.repository.MudItemRepository;
import com.agonyforge.mud.demo.model.repository.MudRoomRepository;
import com.agonyforge.mud.demo.service.CommService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GiveCommandTest {
    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private ApplicationContext applicationContext;

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
    private MudCharacter target;

    @Mock
    private MudItem item;

    @Mock
    private MudItem other;

    @BeforeEach
    void setUp() {
        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        lenient().when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
        lenient().when(repositoryBundle.getRoomRepository()).thenReturn(roomRepository);
    }

    @Test
    void testGiveNoArgs() {
        UUID chId = UUID.randomUUID();
        Long roomId = 100L;

        when(ch.getRoomId()).thenReturn(roomId);
        when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));

        Output output = new Output();
        GiveCommand uut = new GiveCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("GIVE"),
            new Input("g"),
            output);

        verifyNoInteractions(itemRepository);

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("Which item"));
    }

    @Test
    void testGiveOneArg() {
        UUID chId = UUID.randomUUID();
        Long roomId = 100L;

        when(ch.getRoomId()).thenReturn(roomId);
        when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));

        Output output = new Output();
        GiveCommand uut = new GiveCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("GIVE", "SPOON"),
            new Input("g sp"),
            output);

        verifyNoInteractions(itemRepository);

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("Who do you want"));
    }

    @Test
    void testGiveNoItem() {
        UUID chId = UUID.randomUUID();
        Long roomId = 100L;

        when(ch.getId()).thenReturn(chId);
        when(ch.getRoomId()).thenReturn(roomId);
        when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(other.getNameList()).thenReturn(List.of("test"));
        when(itemRepository.getByCharacter(eq(chId))).thenReturn(List.of(other));

        Output output = new Output();
        GiveCommand uut = new GiveCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("GIVE", "SPOON", "SPOOK"),
            new Input("g sp sp"),
            output);

        verify(itemRepository).getByCharacter(eq(chId));
        verify(itemRepository, never()).save(eq(item));

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("You don't have anything"));
    }

    @Test
    void testGiveNoTarget() {
        UUID chId = UUID.randomUUID();
        Long roomId = 100L;

        when(ch.getId()).thenReturn(chId);
        when(ch.getRoomId()).thenReturn(roomId);
        when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(item.getNameList()).thenReturn(List.of("spoon"));
        when(other.getNameList()).thenReturn(List.of("test"));
        when(itemRepository.getByCharacter(eq(chId))).thenReturn(List.of(other, item));

        Output output = new Output();
        GiveCommand uut = new GiveCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("GIVE", "SPOON", "SPOOK"),
            new Input("g sp sp"),
            output);

        verify(itemRepository).getByCharacter(eq(chId));
        verify(characterRepository).getByRoom(eq(roomId));
        verify(itemRepository, never()).save(eq(item));

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("You don't see anyone"));
    }

    @Test
    void testGiveWornItem() {
        UUID chId = UUID.randomUUID();
        Long roomId = 100L;

        when(ch.getId()).thenReturn(chId);
        when(ch.getRoomId()).thenReturn(roomId);
        when(target.getName()).thenReturn("Spook");
        when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.of(ch));
        when(characterRepository.getByRoom(eq(roomId))).thenReturn(List.of(target, ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(item.getNameList()).thenReturn(List.of("spoon"));
        when(item.getWorn()).thenReturn(WearSlot.FACE);
        when(other.getNameList()).thenReturn(List.of("test"));
        when(itemRepository.getByCharacter(eq(chId))).thenReturn(List.of(other, item));

        Output output = new Output();
        GiveCommand uut = new GiveCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("GIVE", "SPOON", "SPOOK"),
            new Input("g sp sp"),
            output);

        verify(itemRepository).getByCharacter(eq(chId));
        verify(characterRepository).getByRoom(eq(roomId));
        verify(itemRepository, never()).save(eq(item));

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("remove it first"));
    }

    @Test
    void testGive() {
        UUID chId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        Long roomId = 100L;

        when(ch.getId()).thenReturn(chId);
        when(ch.getRoomId()).thenReturn(roomId);
        when(ch.getName()).thenReturn("Scion");
        when(target.getId()).thenReturn(targetId);
        when(target.getName()).thenReturn("Spook");
        when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.of(ch));
        when(characterRepository.getByRoom(eq(roomId))).thenReturn(List.of(target, ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(item.getNameList()).thenReturn(List.of("spoon"));
        when(item.getShortDescription()).thenReturn("a spoon");
        when(other.getNameList()).thenReturn(List.of("test"));
        when(itemRepository.getByCharacter(eq(chId))).thenReturn(List.of(other, item));

        Output output = new Output();
        GiveCommand uut = new GiveCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("GIVE", "SPOON", "SPOOK"),
            new Input("g sp sp"),
            output);

        verify(itemRepository).getByCharacter(eq(chId));
        verify(characterRepository).getByRoom(eq(roomId));
        verify(item).setCharacterId(eq(targetId));
        verify(itemRepository).save(eq(item));
        verify(commService).sendTo(eq(target), any(Output.class));
        verify(commService).sendToRoom(eq(webSocketContext), eq(roomId), any(Output.class), eq(target));

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("You give a spoon[default] to Spook."));
    }
}
