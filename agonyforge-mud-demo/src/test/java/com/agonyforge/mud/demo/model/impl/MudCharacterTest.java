package com.agonyforge.mud.demo.model.impl;

import com.agonyforge.mud.demo.model.constant.Effort;
import com.agonyforge.mud.demo.model.constant.Stat;
import com.agonyforge.mud.demo.model.constant.WearSlot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MudCharacterTest {
    private final Random random = new Random();

    @Test
    void testBuildInstance() {
        MudCharacterPrototype proto = new MudCharacterPrototype();

        proto.setComplete(true);
        proto.setUsername("principal");
        proto.setId(random.nextLong());
        proto.setName("Scion");
        proto.setSpeciesId(random.nextLong());
        proto.setProfessionId(random.nextLong());

        MudCharacter instance = proto.buildInstance();

        instance.setRoomId(100L);

        assertEquals(proto.getId(), instance.getPrototypeId());
        assertEquals(100L, instance.getRoomId());
        assertEquals(proto.getName(), instance.getName());
        assertEquals(proto.getUsername(), instance.getUsername());
        assertEquals(proto.getSpeciesId(), instance.getSpeciesId());
        assertEquals(proto.getProfessionId(), instance.getProfessionId());
    }

    @Test
    void testId() {
        MudCharacter uut = new MudCharacter();
        Long id = random.nextLong();

        uut.setId(id);

        assertEquals(id, uut.getId());
    }

    @Test
    void testUser() {
        MudCharacterPrototype uut = new MudCharacterPrototype();
        MudCharacter uutInst;
        String user = "user";

        uut.setComplete(true);
        uut.setUsername(user);
        uutInst = uut.buildInstance();

        assertEquals(user, uut.getUsername());
        assertEquals(user, uutInst.getUsername());
    }

    @Test
    void testWebSocketSession() {
        MudCharacterPrototype uut = new MudCharacterPrototype();
        MudCharacter uutInst;
        String user = UUID.randomUUID().toString();
        String webSocketSession = "webSocketSession";

        uut.setComplete(true);
        uut.setUsername(user);
        uutInst = uut.buildInstance();

        uutInst.setRoomId(100L);
        uutInst.setWebSocketSession(webSocketSession);

        assertEquals(webSocketSession, uutInst.getWebSocketSession());
    }

    @Test
    void testZoneId() {
        MudCharacterPrototype uut = new MudCharacterPrototype();
        MudCharacter uutInst;
        Long roomId = 100L;

        uut.setComplete(true);
        uutInst = uut.buildInstance();
        uutInst.setRoomId(roomId);

        assertEquals(1L, uutInst.getZoneId());
    }

    @Test
    void testRoomId() {
        MudCharacterPrototype uut = new MudCharacterPrototype();
        MudCharacter uutInst;
        Long roomId = 100L;

        uut.setComplete(true);
        uutInst = uut.buildInstance();
        uutInst.setRoomId(roomId);

        assertEquals(roomId, uutInst.getRoomId());
    }

    @Test
    void testName() {
        MudCharacter uut = new MudCharacter();
        String name = "name";

        uut.setName(name);

        assertEquals(name, uut.getName());
    }

    @Test
    void testWearSlots() {
        MudCharacter uut = new MudCharacter();

        uut.setWearSlots(List.of(WearSlot.HEAD));

        assertEquals(WearSlot.HEAD, uut.getWearSlots().get(0));
    }

    @Test
    void testCombinedStats() {
        MudCharacter uut = new MudCharacter();

        uut.setBaseStat(Stat.STR, 2);
        uut.setSpeciesStat(Stat.STR, 3);

        assertEquals(5, uut.getStat(Stat.STR));
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

        assertEquals(5, uut.getEffort(Effort.BASIC));
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
}
