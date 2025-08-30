package com.agonyforge.mud.demo.service;

import com.agonyforge.mud.core.service.dice.DiceService;
import com.agonyforge.mud.core.service.timer.TimerEvent;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.cli.command.HitCommand;
import com.agonyforge.mud.demo.model.impl.*;
import com.agonyforge.mud.demo.model.repository.FightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FightServiceTest {
    private static final Random RANDOM = new Random();

    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private CommService commService;

    @Mock
    private DiceService diceService;

    @Mock
    private FightRepository fightRepository;

    @Mock
    private TimerEvent event;

    @Mock
    private Fight fight;

    @Mock
    private MudCharacter attacker, defender;

    @Mock
    private CharacterComponent attackerCharacter, defenderCharacter;

    @Mock
    private LocationComponent attackerLocation, defenderLocation;

    @Mock
    private MudRoom room;

    @Captor
    private ArgumentCaptor<Output> outputCaptor = ArgumentCaptor.forClass(Output.class);

    @BeforeEach
    void setUp() {
        Long roomId = RANDOM.nextLong();

        when(event.getFrequency()).thenReturn(TimeUnit.SECONDS);

        lenient().when(room.getId()).thenReturn(roomId);

        lenient().when(attacker.getCharacter()).thenReturn(attackerCharacter);
        lenient().when(attacker.getLocation()).thenReturn(attackerLocation);
        lenient().when(attackerCharacter.getName()).thenReturn("Attacker");

        lenient().when(attackerLocation.getRoom()).thenReturn(room);

        lenient().when(defender.getCharacter()).thenReturn(defenderCharacter);
        lenient().when(defender.getLocation()).thenReturn(defenderLocation);
        lenient().when(defenderCharacter.getName()).thenReturn("Defender");
        lenient().when(defenderLocation.getRoom()).thenReturn(room);

        lenient().when(fight.getAttacker()).thenReturn(attacker);
        lenient().when(fight.getDefender()).thenReturn(defender);
    }

    @Test
    void testFightWrongFrequency() {
        when(event.getFrequency()).thenReturn(TimeUnit.HOURS);

        FightService uut = new FightService(repositoryBundle, commService, diceService, fightRepository);
        uut.onTimerEvent(event);

        verifyNoInteractions(commService, diceService, fightRepository);
    }

    @Test
    void testFightAttackerVanished() {
        try (MockedStatic<HitCommand> hitCommandStatic = mockStatic(HitCommand.class)) {
            when(fightRepository.findAll()).thenReturn(List.of(fight));
            when(fight.getAttacker()).thenReturn(null);

            FightService uut = new FightService(repositoryBundle, commService, diceService, fightRepository);
            uut.onTimerEvent(event);

            hitCommandStatic.verify(() -> HitCommand.doHit(
                any(RepositoryBundle.class),
                any(DiceService.class),
                any(FightRepository.class),
                any(Output.class), any(Output.class), any(Output.class),
                any(MudCharacter.class), any(MudCharacter.class)
            ), times(0));

            verifyNoInteractions(commService);
            verify(fightRepository).deleteAll(anyList());
        }
    }

    @Test
    void testFightDefenderVanished() {
        try (MockedStatic<HitCommand> hitCommandStatic = mockStatic(HitCommand.class)) {
            when(fightRepository.findAll()).thenReturn(List.of(fight));
            when(fight.getDefender()).thenReturn(null);

            FightService uut = new FightService(repositoryBundle, commService, diceService, fightRepository);
            uut.onTimerEvent(event);

            hitCommandStatic.verify(() -> HitCommand.doHit(
                any(RepositoryBundle.class),
                any(DiceService.class),
                any(FightRepository.class),
                any(Output.class), any(Output.class), any(Output.class),
                any(MudCharacter.class), any(MudCharacter.class)
            ), times(0));

            verifyNoInteractions(commService);
            verify(fightRepository).deleteAll(anyList());
        }
    }

    @Test
    void testFightDifferentRooms() {
        try (MockedStatic<HitCommand> hitCommandStatic = mockStatic(HitCommand.class)) {
            when(fightRepository.findAll()).thenReturn(List.of(fight));
            when(attackerLocation.getRoom()).thenReturn(room);
            when(defenderLocation.getRoom()).thenReturn(mock(MudRoom.class));

            FightService uut = new FightService(repositoryBundle, commService, diceService, fightRepository);
            uut.onTimerEvent(event);

            hitCommandStatic.verify(() -> HitCommand.doHit(
                any(RepositoryBundle.class),
                any(DiceService.class),
                any(FightRepository.class),
                any(Output.class), any(Output.class), any(Output.class),
                any(MudCharacter.class), any(MudCharacter.class)
            ), times(0));

            verifyNoInteractions(commService);
            verify(fightRepository).deleteAll(anyList());
        }
    }

    @Test
    void testFight() {
        try (MockedStatic<HitCommand> hitCommandStatic = mockStatic(HitCommand.class)) {
            when(attackerCharacter.getHitPoints()).thenReturn(10);
            when(defenderCharacter.getHitPoints()).thenReturn(10);
            when(fightRepository.findAll()).thenReturn(List.of(fight));

            FightService uut = new FightService(repositoryBundle, commService, diceService, fightRepository);
            uut.onTimerEvent(event);

            hitCommandStatic.verify(() -> HitCommand.doHit(
                any(RepositoryBundle.class),
                any(DiceService.class),
                any(FightRepository.class),
                any(Output.class), any(Output.class), any(Output.class),
                any(MudCharacter.class), any(MudCharacter.class)
            ), times(2));

            verify(commService).sendTo(eq(attacker), outputCaptor.capture());
            verify(commService).sendTo(eq(defender), outputCaptor.capture());
            verify(commService).sendToRoom(anyLong(), outputCaptor.capture(), eq(attacker), eq(defender));
            verify(fightRepository, never()).deleteAll(anyList());

            assertEquals(3, outputCaptor.getAllValues().size());
        }
    }

    @Test
    void testFightAttackerDead() {
        try (MockedStatic<HitCommand> hitCommandStatic = mockStatic(HitCommand.class)) {
            when(attackerCharacter.getHitPoints()).thenReturn(0);
            lenient().when(defenderCharacter.getHitPoints()).thenReturn(10);
            when(fightRepository.findAll()).thenReturn(List.of(fight));

            FightService uut = new FightService(repositoryBundle, commService, diceService, fightRepository);
            uut.onTimerEvent(event);

            hitCommandStatic.verify(() -> HitCommand.doHit(
                any(RepositoryBundle.class),
                any(DiceService.class),
                any(FightRepository.class),
                any(Output.class), any(Output.class), any(Output.class),
                any(MudCharacter.class), any(MudCharacter.class)
            ), times(2));

            verify(commService).sendTo(eq(attacker), outputCaptor.capture());
            verify(commService).sendTo(eq(defender), outputCaptor.capture());
            verify(commService).sendToRoom(anyLong(), outputCaptor.capture(), eq(attacker), eq(defender));
            verify(fightRepository).deleteAll(anyList());

            assertEquals(3, outputCaptor.getAllValues().size());
        }
    }

    @Test
    void testFightDefenderDead() {
        try (MockedStatic<HitCommand> hitCommandStatic = mockStatic(HitCommand.class)) {
            when(attackerCharacter.getHitPoints()).thenReturn(10);
            when(defenderCharacter.getHitPoints()).thenReturn(0);
            when(fightRepository.findAll()).thenReturn(List.of(fight));

            FightService uut = new FightService(repositoryBundle, commService, diceService, fightRepository);
            uut.onTimerEvent(event);

            hitCommandStatic.verify(() -> HitCommand.doHit(
                any(RepositoryBundle.class),
                any(DiceService.class),
                any(FightRepository.class),
                any(Output.class), any(Output.class), any(Output.class),
                eq(attacker), eq(defender)
            ), times(1));

            verify(commService).sendTo(eq(attacker), outputCaptor.capture());
            verify(commService).sendTo(eq(defender), outputCaptor.capture());
            verify(commService).sendToRoom(anyLong(), outputCaptor.capture(), eq(attacker), eq(defender));
            verify(fightRepository).deleteAll(anyList());

            assertEquals(3, outputCaptor.getAllValues().size());
        }
    }
}
