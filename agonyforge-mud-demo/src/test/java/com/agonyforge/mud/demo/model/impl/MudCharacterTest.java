package com.agonyforge.mud.demo.model.impl;

import com.agonyforge.mud.demo.model.constant.Effort;
import com.agonyforge.mud.demo.model.constant.Pronoun;
import com.agonyforge.mud.demo.model.constant.Stat;
import com.agonyforge.mud.demo.model.constant.WearSlot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class MudCharacterTest {
    private final Random random = new Random();

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
        String user = UUID.randomUUID().toString();
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
        String name = "name";

        uut.setName(name);

        assertEquals(name, uut.getName());
    }

    @Test
    void testPronoun() {
        MudCharacter uut = new MudCharacter();
        Pronoun pronoun = Pronoun.THEY;

        uut.setPronoun(pronoun);

        assertEquals(pronoun, uut.getPronoun());
    }

    @Test
    void testWearSlots() {
        MudCharacter uut = new MudCharacter();

        uut.setWearSlots(Set.of(WearSlot.HEAD));

        assertTrue(uut.getWearSlots().contains(WearSlot.HEAD));
    }

    @Test
    void testCombinedStats() {
        MudCharacter uut = new MudCharacter();

        uut.setBaseStat(Stat.STR, 2);
        uut.setSpeciesStat(Stat.STR, 3);
        uut.setProfessionStat(Stat.STR, 1);

        assertEquals(6, uut.getStat(Stat.STR));
    }

    @Test
    void testBaseStats() {
        MudCharacter uut = new MudCharacter();

        uut.setBaseStat(Stat.STR, 1);

        assertEquals(1, uut.getBaseStat(Stat.STR));
    }

    @Test
    void testSetAndAddBaseStats() {
        MudCharacter uut = new MudCharacter();

        uut.setBaseStat(Stat.STR, 3);
        uut.setBaseStat(Stat.DEX, 3);

        assertEquals(3, uut.getBaseStat(Stat.STR));
        assertEquals(3, uut.getBaseStat(Stat.DEX));

        uut.addBaseStat(Stat.STR, 2);
        uut.addBaseStat(Stat.DEX, -2);

        assertEquals(5, uut.getBaseStat(Stat.STR));
        assertEquals(1, uut.getBaseStat(Stat.DEX));
    }

    @Test
    void testSpeciesStats() {
        MudCharacter uut = new MudCharacter();

        uut.setSpeciesStat(Stat.STR, 1);

        assertEquals(1, uut.getSpeciesStat(Stat.STR));
    }

    @Test
    void testSetAndAddSpeciesStats() {
        MudCharacter uut = new MudCharacter();

        uut.setSpeciesStat(Stat.STR, 3);
        uut.setSpeciesStat(Stat.DEX, 3);

        assertEquals(3, uut.getSpeciesStat(Stat.STR));
        assertEquals(3, uut.getSpeciesStat(Stat.DEX));

        uut.addSpeciesStat(Stat.STR, 2);
        uut.addSpeciesStat(Stat.DEX, -2);

        assertEquals(5, uut.getSpeciesStat(Stat.STR));
        assertEquals(1, uut.getSpeciesStat(Stat.DEX));
    }

    @Test
    void testProfessionStats() {
        MudCharacter uut = new MudCharacter();

        uut.setProfessionStat(Stat.STR, 1);

        assertEquals(1, uut.getProfessionStat(Stat.STR));
    }

    @Test
    void testSetAndAddProfessionStats() {
        MudCharacter uut = new MudCharacter();

        uut.setProfessionStat(Stat.STR, 3);
        uut.setProfessionStat(Stat.DEX, 3);

        assertEquals(3, uut.getProfessionStat(Stat.STR));
        assertEquals(3, uut.getProfessionStat(Stat.DEX));

        uut.addProfessionStat(Stat.STR, 2);
        uut.addProfessionStat(Stat.DEX, -2);

        assertEquals(5, uut.getProfessionStat(Stat.STR));
        assertEquals(1, uut.getProfessionStat(Stat.DEX));
    }

    @Test
    void testDefense() {
        MudCharacter uut = new MudCharacter();

        uut.setBaseStat(Stat.CON, 3);
        uut.setSpeciesStat(Stat.CON, 2);

        assertEquals(3, uut.getBaseStat(Stat.CON));
        assertEquals(2, uut.getSpeciesStat(Stat.CON));
        assertEquals(5, uut.getDefense());
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
    void testSpeciesId() {
        Long id = random.nextLong();
        MudCharacter uut = new MudCharacter();

        uut.setSpeciesId(id);

        assertEquals(id, uut.getSpeciesId());
    }

    @Test
    void testProfessionId() {
        Long id = random.nextLong();
        MudCharacter uut = new MudCharacter();

        uut.setProfessionId(id);

        assertEquals(id, uut.getProfessionId());
    }
}
