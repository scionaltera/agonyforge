package com.agonyforge.mud.demo.model.impl;

import com.agonyforge.mud.demo.model.constant.Effort;
import com.agonyforge.mud.demo.model.constant.Pronoun;
import com.agonyforge.mud.demo.model.constant.Stat;
import com.agonyforge.mud.demo.model.constant.WearSlot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.EnumSet;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MudCharacterTest {
    private final Random random = new Random();

    @Mock
    private MudSpecies species;

    @Mock
    private MudProfession profession;

    @Test
    void testId() {
        MudCharacter uut = new MudCharacter();
        Long id = random.nextLong();

        uut.setId(id);

        assertEquals(id, uut.getId());
    }

    @Test
    void testPrototypeId() {
        MudCharacter uut = new MudCharacter();
        Long prototypeId = random.nextLong();

        uut.setPrototypeId(prototypeId);

        assertEquals(prototypeId, uut.getPrototypeId());
    }

    @Test
    void testUsername() {
        MudCharacter uut = new MudCharacter();
        uut.setPlayer(new PlayerComponent());
        String user = "user";

        uut.getPlayer().setUsername(user);

        assertEquals(user, uut.getPlayer().getUsername());
    }

    @Test
    void testWebSocketSession() {
        MudCharacter uut = new MudCharacter();
        uut.setPlayer(new PlayerComponent());
        String webSocketSession = "webSocketSession";

        uut.getPlayer().setWebSocketSession(webSocketSession);

        assertEquals(webSocketSession, uut.getPlayer().getWebSocketSession());
    }

    @Test
    void testZoneId() {
        MudCharacter uut = new MudCharacter();
        Long roomId = 100L;

        uut.setRoomId(roomId);

        assertEquals(1L, uut.getZoneId());
    }

    @Test
    void testRoomId() {
        MudCharacter uut = new MudCharacter();
        Long roomId = 100L;

        uut.setRoomId(roomId);

        assertEquals(roomId, uut.getRoomId());
    }

    @Test
    void testName() {
        MudCharacter uut = new MudCharacter();
        uut.setCharacter(new CharacterComponent());
        String name = "name";

        uut.getCharacter().setName(name);

        assertEquals(name, uut.getCharacter().getName());
    }

    @Test
    void testPronoun() {
        MudCharacter uut = new MudCharacter();
        uut.setCharacter(new CharacterComponent());
        Pronoun pronoun = Pronoun.THEY;

        uut.getCharacter().setPronoun(pronoun);

        assertEquals(pronoun, uut.getCharacter().getPronoun());
    }

    @Test
    void testWearSlots() {
        MudCharacter uut = new MudCharacter();
        uut.setCharacter(new CharacterComponent());

        uut.getCharacter().setWearSlots(EnumSet.of(WearSlot.HEAD));

        assertTrue(uut.getCharacter().getWearSlots().contains(WearSlot.HEAD));
    }

    @Test
    void testCombinedStats() {
        when(species.getStat(eq(Stat.STR))).thenReturn(3);
        when(profession.getStat(eq(Stat.STR))).thenReturn(1);

        MudCharacter uut = new MudCharacter();

        uut.setCharacter(new CharacterComponent());
        uut.getCharacter().setSpecies(species);
        uut.getCharacter().setProfession(profession);

        uut.getCharacter().setBaseStat(Stat.STR, 2);

        assertEquals(6, uut.getCharacter().getStat(Stat.STR));
    }

    @Test
    void testBaseStats() {
        MudCharacter uut = new MudCharacter();

        uut.setCharacter(new CharacterComponent());
        uut.getCharacter().setBaseStat(Stat.STR, 1);

        assertEquals(1, uut.getCharacter().getBaseStat(Stat.STR));
    }

    @Test
    void testSetAndAddBaseStats() {
        MudCharacter uut = new MudCharacter();

        uut.setCharacter(new CharacterComponent());

        uut.getCharacter().setBaseStat(Stat.STR, 3);
        uut.getCharacter().setBaseStat(Stat.DEX, 3);

        assertEquals(3, uut.getCharacter().getBaseStat(Stat.STR));
        assertEquals(3, uut.getCharacter().getBaseStat(Stat.DEX));

        uut.getCharacter().addBaseStat(Stat.STR, 2);
        uut.getCharacter().addBaseStat(Stat.DEX, -2);

        assertEquals(5, uut.getCharacter().getBaseStat(Stat.STR));
        assertEquals(1, uut.getCharacter().getBaseStat(Stat.DEX));
    }

    @Test
    void testSpeciesStats() {
        when(species.getStat(eq(Stat.STR))).thenReturn(1);

        MudCharacter uut = new MudCharacter();
        uut.setCharacter(new CharacterComponent());
        uut.getCharacter().setSpecies(species);

        assertEquals(1, uut.getCharacter().getSpeciesStat(Stat.STR));
    }

    @Test
    void testProfessionStats() {
        when(profession.getStat(eq(Stat.STR))).thenReturn(1);

        MudCharacter uut = new MudCharacter();
        uut.setCharacter(new CharacterComponent());
        uut.getCharacter().setProfession(profession);

        assertEquals(1, uut.getCharacter().getProfessionStat(Stat.STR));
    }

    @Test
    void testDefense() {
        when(species.getStat(eq(Stat.CON))).thenReturn(2);
        when(profession.getStat(eq(Stat.CON))).thenReturn(1);

        MudCharacter uut = new MudCharacter();
        uut.setCharacter(new CharacterComponent());
        uut.getCharacter().setSpecies(species);
        uut.getCharacter().setProfession(profession);

        uut.getCharacter().setBaseStat(Stat.CON, 3);

        assertEquals(3, uut.getCharacter().getBaseStat(Stat.CON));
        assertEquals(2, uut.getCharacter().getSpeciesStat(Stat.CON));
        assertEquals(1, uut.getCharacter().getProfessionStat(Stat.CON));
        assertEquals(6, uut.getCharacter().getDefense());
    }

    @Test
    void testCombinedEfforts() {
        MudCharacter uut = new MudCharacter();

        uut.setBaseEffort(Effort.BASIC, 2);
        uut.setSpeciesEffort(Effort.BASIC, 3);
        uut.setProfessionEffort(Effort.BASIC, 1);

        assertEquals(6, uut.getEffort(Effort.BASIC));
    }

    @Test
    void testBaseEfforts() {
        MudCharacter uut = new MudCharacter();

        uut.setBaseEffort(Effort.GUNS, 1);

        assertEquals(1, uut.getBaseEffort(Effort.GUNS));
    }

    @Test
    void testSetAndAddBaseEffort() {
        MudCharacter uut = new MudCharacter();

        uut.setBaseEffort(Effort.BASIC, 3);
        uut.setBaseEffort(Effort.GUNS, 3);

        assertEquals(3, uut.getBaseEffort(Effort.BASIC));
        assertEquals(3, uut.getBaseEffort(Effort.GUNS));

        uut.addBaseEffort(Effort.BASIC, 2);
        uut.addBaseEffort(Effort.GUNS, -2);

        assertEquals(5, uut.getBaseEffort(Effort.BASIC));
        assertEquals(1, uut.getBaseEffort(Effort.GUNS));
    }

    @Test
    void testSpeciesEfforts() {
        MudCharacter uut = new MudCharacter();

        uut.setSpeciesEffort(Effort.GUNS, 1);

        assertEquals(1, uut.getSpeciesEffort(Effort.GUNS));
    }

    @Test
    void testSetAndAddSpeciesEffort() {
        MudCharacter uut = new MudCharacter();

        uut.setSpeciesEffort(Effort.BASIC, 3);
        uut.setSpeciesEffort(Effort.GUNS, 3);

        assertEquals(3, uut.getSpeciesEffort(Effort.BASIC));
        assertEquals(3, uut.getSpeciesEffort(Effort.GUNS));

        uut.addSpeciesEffort(Effort.BASIC, 2);
        uut.addSpeciesEffort(Effort.GUNS, -2);

        assertEquals(5, uut.getSpeciesEffort(Effort.BASIC));
        assertEquals(1, uut.getSpeciesEffort(Effort.GUNS));
    }

    @Test
    void testProfessionEfforts() {
        MudCharacter uut = new MudCharacter();

        uut.setSpeciesEffort(Effort.GUNS, 1);

        assertEquals(1, uut.getSpeciesEffort(Effort.GUNS));
    }

    @Test
    void testSetAndAddProfessionEffort() {
        MudCharacter uut = new MudCharacter();

        uut.setProfessionEffort(Effort.BASIC, 3);
        uut.setProfessionEffort(Effort.GUNS, 3);

        assertEquals(3, uut.getProfessionEffort(Effort.BASIC));
        assertEquals(3, uut.getProfessionEffort(Effort.GUNS));

        uut.addProfessionEffort(Effort.BASIC, 2);
        uut.addProfessionEffort(Effort.GUNS, -2);

        assertEquals(5, uut.getProfessionEffort(Effort.BASIC));
        assertEquals(1, uut.getProfessionEffort(Effort.GUNS));
    }

    @Test
    void testCharacterComponent() {
        CharacterComponent component = new CharacterComponent();
        MudCharacter uut = new MudCharacter();

        uut.setCharacter(component);

        assertEquals(component, uut.getCharacter());
    }
}
