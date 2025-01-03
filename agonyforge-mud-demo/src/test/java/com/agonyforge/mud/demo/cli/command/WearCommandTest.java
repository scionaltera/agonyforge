package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.constant.WearMode;
import com.agonyforge.mud.demo.model.constant.WearSlot;
import com.agonyforge.mud.demo.model.impl.*;
import com.agonyforge.mud.demo.model.constant.Pronoun;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import com.agonyforge.mud.demo.model.repository.MudItemRepository;
import com.agonyforge.mud.demo.model.repository.MudRoomRepository;
import com.agonyforge.mud.demo.service.CommService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.*;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WearCommandTest {
    @Mock
    private ApplicationContext applicationContext;

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
    private CharacterComponent characterComponent;

    @Mock
    private MudItem item;

    @Mock
    private MudItem target;

    @Mock
    private ItemComponent itemItemComponent, targetItemComponent;

    @Mock
    private LocationComponent itemLocationComponent, targetLocationComponent, chLocationComponent;

    @Mock
    private MudRoom room;

    private final Random random = new Random();

    @BeforeEach
    void setUp() {
        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        lenient().when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
        lenient().when(repositoryBundle.getRoomRepository()).thenReturn(roomRepository);
        lenient().when(itemItemComponent.getWearMode()).thenReturn(WearMode.ALL);
        lenient().when(targetItemComponent.getWearMode()).thenReturn(WearMode.ALL);
    }

    @Test
    void testWearNoArg() {
        Long chId = random.nextLong();

        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);

        Output output = new Output();
        WearCommand uut = new WearCommand(repositoryBundle, commService, applicationContext);
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
        Long chId = random.nextLong();

        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);

        Output output = new Output();
        WearCommand uut = new WearCommand(repositoryBundle, commService, applicationContext);
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
    void testWearTargetNotWearable() {
        Long chId = random.nextLong();

        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(itemRepository.findByLocationHeld(ch)).thenReturn(List.of(target));
        when(target.getLocation()).thenReturn(targetLocationComponent);
        when(target.getLocation().getWorn()).thenReturn(EnumSet.noneOf(WearSlot.class));
        when(target.getItem()).thenReturn(targetItemComponent);
        when(target.getItem().getNameList()).thenReturn(Set.of("hat"));
        when(target.getItem().getWearSlots()).thenReturn(EnumSet.noneOf(WearSlot.class));
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);

        Output output = new Output();
        WearCommand uut = new WearCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("WEAR", "HAT"),
            new Input("wea ha"),
            output);

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("You can't wear that"));
    }

    @Test
    void testWearTargetAlreadyWorn() {
        Long chId = random.nextLong();

        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);
        when(itemRepository.findByLocationHeld(ch)).thenReturn(List.of(target));
        when(target.getLocation()).thenReturn(targetLocationComponent);
        when(target.getLocation().getWorn()).thenReturn(EnumSet.of(WearSlot.HEAD));

        Output output = new Output();
        WearCommand uut = new WearCommand(repositoryBundle, commService, applicationContext);
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
    void testWearTargetNoAvailableSlot() {
        Long chId = random.nextLong();

        when(ch.getCharacter()).thenReturn(characterComponent);
        when(ch.getCharacter().getWearSlots()).thenReturn(EnumSet.allOf(WearSlot.class));
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(itemRepository.findByLocationHeld(ch)).thenReturn(List.of(item, target));

        when(item.getLocation()).thenReturn(itemLocationComponent);
        when(item.getLocation().getWorn()).thenReturn(EnumSet.of(WearSlot.HEAD));

        when(target.getItem()).thenReturn(targetItemComponent);
        when(target.getItem().getNameList()).thenReturn(Set.of("hat"));
        when(target.getItem().getWearSlots()).thenReturn(EnumSet.of(WearSlot.HEAD));
        when(target.getLocation()).thenReturn(targetLocationComponent);
        when(target.getLocation().getWorn()).thenReturn(EnumSet.noneOf(WearSlot.class));

        Output output = new Output();
        WearCommand uut = new WearCommand(repositoryBundle, commService, applicationContext);
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
        Long chId = random.nextLong();

        when(ch.getCharacter()).thenReturn(characterComponent);
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(itemRepository.findByLocationHeld(ch)).thenReturn(List.of(target));
        when(ch.getCharacter().getWearSlots()).thenReturn(EnumSet.of(WearSlot.HEAD));
        when(ch.getCharacter().getPronoun()).thenReturn(Pronoun.SHE);
        when(target.getLocation()).thenReturn(targetLocationComponent);
        when(target.getLocation().getWorn()).thenReturn(EnumSet.noneOf(WearSlot.class));
        when(target.getItem()).thenReturn(targetItemComponent);
        when(target.getItem().getShortDescription()).thenReturn("a test hat");
        when(target.getItem().getNameList()).thenReturn(Set.of("hat"));
        when(target.getItem().getWearSlots()).thenReturn(EnumSet.of(WearSlot.HEAD));
        when(room.getId()).thenReturn(100L);
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);

        Output output = new Output();
        WearCommand uut = new WearCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("WEAR", "HAT"),
            new Input("wea ha"),
            output);

        verify(targetLocationComponent).setHeld(eq(ch));
        verify(targetLocationComponent).setRoom(eq(null));
        verify(targetLocationComponent).setWorn(ArgumentMatchers.any());
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
        Long chId = random.nextLong();

        when(ch.getCharacter()).thenReturn(characterComponent);
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));

        when(itemRepository.findByLocationHeld(ch)).thenReturn(List.of(item, target));
        when(room.getId()).thenReturn(100L);

        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);
        when(ch.getCharacter().getWearSlots()).thenReturn(EnumSet.of(WearSlot.HELD_OFF, WearSlot.HELD_MAIN, WearSlot.HEAD));
        when(ch.getCharacter().getPronoun()).thenReturn(Pronoun.SHE);

        when(item.getLocation()).thenReturn(itemLocationComponent);
        when(item.getLocation().getWorn()).thenReturn(EnumSet.of(WearSlot.HELD_OFF));

        when(target.getItem()).thenReturn(targetItemComponent);
        when(target.getItem().getNameList()).thenReturn(Set.of("hat"));
        when(target.getItem().getShortDescription()).thenReturn("a test hat");
        when(target.getItem().getWearSlots()).thenReturn(EnumSet.of(WearSlot.HEAD));
        when(target.getLocation()).thenReturn(targetLocationComponent);
        when(target.getLocation().getWorn()).thenReturn(EnumSet.noneOf(WearSlot.class));

        Output output = new Output();
        WearCommand uut = new WearCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("WEAR", "HAT"),
            new Input("wea ha"),
            output);

        verify(targetLocationComponent).setWorn(eq(EnumSet.of(WearSlot.HEAD)));
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
        Long chId = random.nextLong();

        when(ch.getCharacter()).thenReturn(characterComponent);
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(itemRepository.findByLocationHeld(ch)).thenReturn(List.of(item, target));
        when(room.getId()).thenReturn(100L);

        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);
        when(ch.getCharacter().getWearSlots()).thenReturn(EnumSet.of(WearSlot.HELD_OFF, WearSlot.HELD_MAIN, WearSlot.HEAD));
        when(ch.getCharacter().getPronoun()).thenReturn(Pronoun.SHE);

        when(item.getLocation()).thenReturn(itemLocationComponent);
        when(item.getLocation().getWorn()).thenReturn(EnumSet.of(WearSlot.HELD_OFF));

        when(target.getLocation()).thenReturn(targetLocationComponent);
        when(target.getLocation().getWorn()).thenReturn(EnumSet.noneOf(WearSlot.class));
        when(target.getItem()).thenReturn(targetItemComponent);
        when(target.getItem().getShortDescription()).thenReturn("a test hat");
        when(target.getItem().getNameList()).thenReturn(Set.of("hat"));
        when(target.getItem().getWearSlots()).thenReturn(EnumSet.of(WearSlot.HEAD));

        Output output = new Output();
        WearCommand uut = new WearCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("WEAR", "HAT"),
            new Input("wea ha"),
            output);

        verify(targetLocationComponent).setWorn(ArgumentMatchers.<EnumSet<WearSlot>>any());
        verify(itemRepository).save(any(MudItem.class));
        verify(commService).sendToRoom(
            eq(webSocketContext),
            anyLong(),
            any(Output.class));

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("You wear a test hat[default] on your head"));
    }
}
