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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MudCharacterPrototypeTest {
    private final Random random = new Random();

    @Mock
    private MudSpecies species;

    @Mock
    private MudProfession profession;

    @Mock
    private MudRoom room;

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

        instance.getLocation().setRoom(room);

        assertEquals(proto.getId(), instance.getTemplate().getId());
        assertEquals(room, instance.getLocation().getRoom());
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
        uut.getCharacter().setSpecies(species);

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
        uut.getCharacter().setSpecies(species);

        MudCharacter uutInst;
        String user = UUID.randomUUID().toString();
        String webSocketSession = "webSocketSession";

        uut.setComplete(true);
        uut.getPlayer().setUsername(user);
        uutInst = uut.buildInstance();

        uutInst.getLocation().setRoom(room);
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
        when(species.getStat(Stat.STR)).thenReturn(3);
        when(profession.getStat(Stat.STR)).thenReturn(1);

        MudCharacterPrototype uut = new MudCharacterPrototype();

        uut.setCharacter(new CharacterComponent());
        uut.getCharacter().setSpecies(species);
        uut.getCharacter().setProfession(profession);

        uut.getCharacter().setBaseStat(Stat.STR, 2);

        assertEquals(6, uut.getCharacter().getStat(Stat.STR));
    }

    @Test
    void testBaseStats() {
        MudCharacterPrototype uut = new MudCharacterPrototype();

        uut.setCharacter(new CharacterComponent());
        uut.getCharacter().setBaseStat(Stat.STR, 1);

        assertEquals(1, uut.getCharacter().getBaseStat(Stat.STR));
    }

    @Test
    void testSetAndAddBaseStats() {
        MudCharacterPrototype uut = new MudCharacterPrototype();

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
        when(species.getStat(Stat.STR)).thenReturn(1);

        MudCharacterPrototype uut = new MudCharacterPrototype();

        uut.setCharacter(new CharacterComponent());
        uut.getCharacter().setSpecies(species);

        assertEquals(1, uut.getCharacter().getSpeciesStat(Stat.STR));
    }

    @Test
    void testProfessionStats() {
        when(profession.getStat(Stat.STR)).thenReturn(1);

        MudCharacterPrototype uut = new MudCharacterPrototype();

        uut.setCharacter(new CharacterComponent());
        uut.getCharacter().setProfession(profession);

        assertEquals(1, uut.getCharacter().getProfessionStat(Stat.STR));
    }

    @Test
    void testDefense() {
        when(species.getStat(eq(Stat.CON))).thenReturn(2);
        when(profession.getStat(eq(Stat.CON))).thenReturn(1);

        MudCharacterPrototype uut = new MudCharacterPrototype();

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
        when(species.getEffort(Effort.BASIC)).thenReturn(3);
        when(profession.getEffort(Effort.BASIC)).thenReturn(1);

        MudCharacterPrototype uut = new MudCharacterPrototype();
        uut.setCharacter(new CharacterComponent());
        uut.getCharacter().setSpecies(species);
        uut.getCharacter().setProfession(profession);

        uut.getCharacter().setBaseEffort(Effort.BASIC, 2);

        assertEquals(6, uut.getCharacter().getEffort(Effort.BASIC));
    }

    @Test
    void testBaseEfforts() {
        MudCharacterPrototype uut = new MudCharacterPrototype();
        uut.setCharacter(new CharacterComponent());

        uut.getCharacter().setBaseEffort(Effort.GUNS, 1);

        assertEquals(1, uut.getCharacter().getBaseEffort(Effort.GUNS));
    }

    @Test
    void testSetAndAddBaseEffort() {
        MudCharacterPrototype uut = new MudCharacterPrototype();

        uut.setCharacter(new CharacterComponent());

        uut.getCharacter().setBaseEffort(Effort.BASIC, 3);
        uut.getCharacter().setBaseEffort(Effort.GUNS, 3);

        assertEquals(3, uut.getCharacter().getBaseEffort(Effort.BASIC));
        assertEquals(3, uut.getCharacter().getBaseEffort(Effort.GUNS));

        uut.getCharacter().addBaseEffort(Effort.BASIC, 2);
        uut.getCharacter().addBaseEffort(Effort.GUNS, -2);

        assertEquals(5, uut.getCharacter().getBaseEffort(Effort.BASIC));
        assertEquals(1, uut.getCharacter().getBaseEffort(Effort.GUNS));
    }

    @Test
    void testSpeciesEfforts() {
        when(species.getEffort(eq(Effort.GUNS))).thenReturn(1);

        MudCharacterPrototype uut = new MudCharacterPrototype();
        uut.setCharacter(new CharacterComponent());
        uut.getCharacter().setSpecies(species);

        assertEquals(1, uut.getCharacter().getSpeciesEffort(Effort.GUNS));
    }

    @Test
    void testProfessionEfforts() {
        when(profession.getEffort(eq(Effort.GUNS))).thenReturn(1);

        MudCharacterPrototype uut = new MudCharacterPrototype();
        uut.setCharacter(new CharacterComponent());
        uut.getCharacter().setProfession(profession);

        assertEquals(1, uut.getCharacter().getProfessionEffort(Effort.GUNS));
    }

    @Test
    void testCharacterComponent() {
        CharacterComponent component = new CharacterComponent();
        MudCharacterPrototype uut = new MudCharacterPrototype();

        uut.setCharacter(component);

        assertEquals(component, uut.getCharacter());
    }
}
