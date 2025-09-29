package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.service.dice.DiceResult;
import com.agonyforge.mud.core.service.dice.DiceService;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.constant.WearSlot;
import com.agonyforge.mud.demo.model.impl.*;
import com.agonyforge.mud.demo.model.repository.FightRepository;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import com.agonyforge.mud.demo.model.repository.MudItemRepository;
import com.agonyforge.mud.demo.service.CommService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.*;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HitCommandTest {
    private static final Random RANDOM = new Random();

    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private CommService commService;

    @Mock
    private DiceService diceService;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private Question question;

    @Mock
    private WebSocketContext webSocketContext;

    @Mock
    private FightRepository fightRepository;

    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private MudCharacter ch, target;

    @Mock
    private LocationComponent chLocationComponent, weaponLocationComponent;

    @Mock
    private CharacterComponent chCharacter, targetCharacter;

    @Mock
    private MudItemRepository itemRepository;

    @Mock
    private MudItem weapon;

    @Mock
    private MudRoom room;

    @BeforeEach
    void setUp() {
        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        lenient().when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
        lenient().when(ch.getLocation()).thenReturn(chLocationComponent);
        lenient().when(chLocationComponent.getRoom()).thenReturn(room);
        lenient().when(ch.getCharacter()).thenReturn(chCharacter);
        lenient().when(chCharacter.getName()).thenReturn("Scion");
        lenient().when(target.getCharacter()).thenReturn(targetCharacter);
        lenient().when(targetCharacter.getName()).thenReturn("Frodo");
        lenient().when(targetCharacter.getHitPoints()).thenReturn(10);
    }

    @Test
    void testHitNoArg() {
        Output output = new Output();
        HitCommand uut = new HitCommand(repositoryBundle, commService, diceService, fightRepository, applicationContext);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("HIT"),
            new Input("hit"),
            output);

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("Who do you want to hit?"));

        verifyNoInteractions(commService);
    }

    @Test
    void testHitNoTarget() {
        Long chId = RANDOM.nextLong();

        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));

        Output output = new Output();
        HitCommand uut = new HitCommand(repositoryBundle, commService, diceService, fightRepository, applicationContext);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("HIT", "FRODO"),
            new Input("hit frodo"),
            output);

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("You don't see anyone like that here."));

        verifyNoInteractions(commService);
    }

    @Test
    void testHitTargetMiss() {
        Long chId = RANDOM.nextLong();
        Long roomId = RANDOM.nextLong();

        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(room.getId()).thenReturn(roomId);
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(characterRepository.findByLocationRoom(eq(room))).thenReturn(List.of(ch, target));
        when(diceService.roll(eq(1), eq(20), eq(0))).thenReturn(new DiceResult(20, 0, 11));

        Output output = new Output();
        HitCommand uut = new HitCommand(repositoryBundle, commService, diceService, fightRepository, applicationContext);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("HIT", "FRODO"),
            new Input("hit frodo"),
            output);

        assertEquals(question, result);
        assertTrue(output.getOutput().stream().anyMatch(s -> s.contains("You miss.")));

        verify(commService).sendTo(eq(target), any(Output.class));
        verify(commService).sendToRoom(eq(roomId), any(Output.class), eq(ch), eq(target));
        verify(diceService).roll(eq(1), eq(20), eq(0));
        verify(diceService, never()).roll(eq(1), eq(4));
        verify(targetCharacter, never()).setHitPoints(anyInt());
    }

    @Test
    void testHitTargetHitUnarmed() {
        Long chId = RANDOM.nextLong();
        Long roomId = RANDOM.nextLong();

        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(room.getId()).thenReturn(roomId);
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(characterRepository.findByLocationRoom(eq(room))).thenReturn(List.of(ch, target));
        when(diceService.roll(eq(1), eq(20), eq(0))).thenReturn(new DiceResult(20, 0, 12));
        when(diceService.roll(eq(1), eq(4))).thenReturn(new DiceResult(4, 0, 4));

        when(itemRepository.findByLocationHeld(eq(ch))).thenReturn(List.of());

        Output output = new Output();
        HitCommand uut = new HitCommand(repositoryBundle, commService, diceService, fightRepository, applicationContext);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("HIT", "FRODO"),
            new Input("hit frodo"),
            output);

        assertEquals(question, result);
        assertTrue(output.getOutput().stream().anyMatch(s -> s.contains("You hit Frodo!")));

        verify(commService).sendTo(eq(target), any(Output.class));
        verify(commService).sendToRoom(eq(roomId), any(Output.class), eq(ch), eq(target));
        verify(diceService).roll(eq(1), eq(20), eq(0));
        verify(diceService).roll(eq(1), eq(4));
        verify(diceService, never()).roll(eq(1), eq(12));
        verify(targetCharacter).setHitPoints(eq(6));
    }

    @Test
    void testHitTargetHitWithWeapon() {
        Long chId = RANDOM.nextLong();
        Long roomId = RANDOM.nextLong();

        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(room.getId()).thenReturn(roomId);
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(characterRepository.findByLocationRoom(eq(room))).thenReturn(List.of(ch, target));
        when(diceService.roll(eq(1), eq(20), eq(0))).thenReturn(new DiceResult(20, 0, 12));
        when(diceService.roll(eq(1), eq(6))).thenReturn(new DiceResult(6, 0, 4));

        when(itemRepository.findByLocationHeld(eq(ch))).thenReturn(List.of(weapon));
        when(weapon.getLocation()).thenReturn(weaponLocationComponent);
        when(weaponLocationComponent.getWorn()).thenReturn(EnumSet.of(WearSlot.HELD_MAIN));

        Output output = new Output();
        HitCommand uut = new HitCommand(repositoryBundle, commService, diceService, fightRepository, applicationContext);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("HIT", "FRODO"),
            new Input("hit frodo"),
            output);

        assertEquals(question, result);
        assertTrue(output.getOutput().stream().anyMatch(s -> s.contains("You hit Frodo!")));

        verify(commService).sendTo(eq(target), any(Output.class));
        verify(commService).sendToRoom(eq(roomId), any(Output.class), eq(ch), eq(target));
        verify(diceService).roll(eq(1), eq(20), eq(0));
        verify(diceService).roll(eq(1), eq(6));
        verify(diceService, never()).roll(eq(1), eq(12), eq(0));
        verify(targetCharacter).setHitPoints(eq(6));
        verify(characterRepository).save(eq(target));
    }

    @Test
    void testHitTargetHitUltimate() {
        Long chId = RANDOM.nextLong();
        Long roomId = RANDOM.nextLong();

        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(room.getId()).thenReturn(roomId);
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(characterRepository.findByLocationRoom(eq(room))).thenReturn(List.of(ch, target));
        when(diceService.roll(eq(1), eq(20), eq(0))).thenReturn(new DiceResult(20, 0, 20));
        when(diceService.roll(eq(1), eq(4))).thenReturn(new DiceResult(4, 0, 4));
        when(diceService.roll(eq(1), eq(12), eq(0))).thenReturn(new DiceResult(12, 1, 8));

        when(itemRepository.findByLocationHeld(eq(ch))).thenReturn(List.of());

        Output output = new Output();
        HitCommand uut = new HitCommand(repositoryBundle, commService, diceService, fightRepository, applicationContext);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("HIT", "FRODO"),
            new Input("hit frodo"),
            output);

        assertEquals(question, result);
        assertTrue(output.getOutput().stream().anyMatch(s -> s.contains("You hit Frodo!")));

        verify(commService).sendTo(eq(target), any(Output.class));
        verify(commService).sendToRoom(eq(roomId), any(Output.class), eq(ch), eq(target));
        verify(diceService).roll(eq(1), eq(20), eq(0));
        verify(diceService).roll(eq(1), eq(4));
        verify(diceService).roll(eq(1), eq(12), eq(0));
        verify(targetCharacter).setHitPoints(eq(0));
        verify(characterRepository).save(eq(target));
    }
}
