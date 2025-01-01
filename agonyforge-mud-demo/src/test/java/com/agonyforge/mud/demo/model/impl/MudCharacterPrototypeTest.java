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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class MudCharacterPrototypeTest {
    private final Random random = new Random();

    @Mock
    private MudSpecies species;

    @Mock
    private MudProfession profession;

    @Test
    void testBuildInstance() {
        MudCharacterPrototype proto = new MudCharacterPrototype();
        proto.setPlayer(new PlayerComponent());
        proto.setCharacter(new CharacterComponent());

        proto.setComplete(true);
        proto.getPlayer().setUsername("principal");
        proto.setId(random.nextLong());
        proto.getCharacter().setName("Scion");
        proto.getCharacter().setSpecies(species);
        proto.getCharacter().setProfession(profession);

        MudCharacter instance = proto.buildInstance();

        instance.setRoomId(100L);

        assertEquals(proto.getId(), instance.getPrototypeId());
        assertEquals(100L, instance.getRoomId());
        assertEquals(proto.getCharacter().getName(), instance.getCharacter().getName());
        assertEquals(proto.getPlayer().getUsername(), instance.getPlayer().getUsername());
        assertEquals(proto.getCharacter().getSpecies(), instance.getCharacter().getSpecies());
        assertEquals(proto.getCharacter().getProfession(), instance.getCharacter().getProfession());
    }

    @Test
    void testId() {
        MudCharacterPrototype uut = new MudCharacterPrototype();
        Long id = random.nextLong();

        uut.setId(id);

        assertEquals(id, uut.getId());
    }

    @Test
    void testUsername() {
        MudCharacterPrototype uut = new MudCharacterPrototype();
        uut.setPlayer(new PlayerComponent());
        uut.setCharacter(new CharacterComponent());

        MudCharacter uutInst;
        String user = "user";

        uut.setComplete(true);
        uut.getPlayer().setUsername(user);
        uutInst = uut.buildInstance();

        assertEquals(user, uut.getPlayer().getUsername());
        assertEquals(user, uutInst.getPlayer().getUsername());
    }

    @Test
    void testWebSocketSession() {
        MudCharacterPrototype uut = new MudCharacterPrototype();
        uut.setPlayer(new PlayerComponent());
        uut.setCharacter(new CharacterComponent());

        MudCharacter uutInst;
        String user = UUID.randomUUID().toString();
        String webSocketSession = "webSocketSession";

        uut.setComplete(true);
        uut.getPlayer().setUsername(user);
        uutInst = uut.buildInstance();

        uutInst.setRoomId(100L);
        uutInst.getPlayer().setWebSocketSession(webSocketSession);

        assertEquals(webSocketSession, uutInst.getPlayer().getWebSocketSession());
    }

    @Test
    void testName() {
        MudCharacterPrototype uut = new MudCharacterPrototype();
        uut.setCharacter(new CharacterComponent());
        String name = "name";

        uut.getCharacter().setName(name);

        assertEquals(name, uut.getCharacter().getName());
    }

    @Test
    void testPronoun() {
        MudCharacterPrototype uut = new MudCharacterPrototype();
        uut.setCharacter(new CharacterComponent());
        Pronoun pronoun = Pronoun.IT;

        uut.getCharacter().setPronoun(pronoun);

        assertEquals(pronoun, uut.getCharacter().getPronoun());
    }

    @Test
    void testWearSlots() {
        MudCharacterPrototype uut = new MudCharacterPrototype();
        uut.setCharacter(new CharacterComponent());

        uut.getCharacter().setWearSlots(EnumSet.of(WearSlot.HEAD));

        assertTrue(uut.getCharacter().getWearSlots().contains(WearSlot.HEAD));
    }

    @Test
    void testCombinedStats() {
        MudCharacterPrototype uut = new MudCharacterPrototype();

        uut.setBaseStat(Stat.STR, 2);
        uut.setSpeciesStat(Stat.STR, 3);
        uut.setProfessionStat(Stat.STR, 1);

        assertEquals(6, uut.getStat(Stat.STR));
    }

    @Test
    void testBaseStats() {
        MudCharacterPrototype uut = new MudCharacterPrototype();

        uut.setBaseStat(Stat.STR, 1);

        assertEquals(1, uut.getBaseStat(Stat.STR));
    }

    @Test
    void testSetAndAddBaseStats() {
        MudCharacterPrototype uut = new MudCharacterPrototype();

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
        MudCharacterPrototype uut = new MudCharacterPrototype();

        uut.setSpeciesStat(Stat.STR, 1);

        assertEquals(1, uut.getSpeciesStat(Stat.STR));
    }

    @Test
    void testSetAndAddSpeciesStats() {
        MudCharacterPrototype uut = new MudCharacterPrototype();

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
        MudCharacterPrototype uut = new MudCharacterPrototype();

        uut.setProfessionStat(Stat.STR, 1);

        assertEquals(1, uut.getProfessionStat(Stat.STR));
    }

    @Test
    void testSetAndAddProfessionStats() {
        MudCharacterPrototype uut = new MudCharacterPrototype();

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
        MudCharacterPrototype uut = new MudCharacterPrototype();

        uut.setBaseStat(Stat.CON, 3);
        uut.setSpeciesStat(Stat.CON, 2);

        assertEquals(3, uut.getBaseStat(Stat.CON));
        assertEquals(2, uut.getSpeciesStat(Stat.CON));
        assertEquals(5, uut.getDefense());
    }

    @Test
    void testCombinedEfforts() {
        MudCharacterPrototype uut = new MudCharacterPrototype();

        uut.setBaseEffort(Effort.BASIC, 2);
        uut.setSpeciesEffort(Effort.BASIC, 3);
        uut.setProfessionEffort(Effort.BASIC, 1);

        assertEquals(6, uut.getEffort(Effort.BASIC));
    }

    @Test
    void testBaseEfforts() {
        MudCharacterPrototype uut = new MudCharacterPrototype();

        uut.setBaseEffort(Effort.GUNS, 1);

        assertEquals(1, uut.getBaseEffort(Effort.GUNS));
    }

    @Test
    void testSetAndAddBaseEffort() {
        MudCharacterPrototype uut = new MudCharacterPrototype();

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
        MudCharacterPrototype uut = new MudCharacterPrototype();

        uut.setSpeciesEffort(Effort.GUNS, 1);

        assertEquals(1, uut.getSpeciesEffort(Effort.GUNS));
    }

    @Test
    void testSetAndAddSpeciesEffort() {
        MudCharacterPrototype uut = new MudCharacterPrototype();

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
        MudCharacterPrototype uut = new MudCharacterPrototype();

        uut.setProfessionEffort(Effort.GUNS, 1);

        assertEquals(1, uut.getProfessionEffort(Effort.GUNS));
    }

    @Test
    void testSetAndAddProfessionEffort() {
        MudCharacterPrototype uut = new MudCharacterPrototype();

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
        MudCharacterPrototype uut = new MudCharacterPrototype();

        uut.setCharacter(component);

        assertEquals(component, uut.getCharacter());
    }
}
