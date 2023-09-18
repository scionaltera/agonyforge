package com.agonyforge.mud.demo.model.impl;

import com.agonyforge.mud.demo.model.constant.Effort;
import com.agonyforge.mud.demo.model.constant.Stat;
import com.agonyforge.mud.demo.model.constant.WearSlot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class MudCharacterTest {
    @Test
    void testBuildInstance() {
        MudCharacter proto = new MudCharacter();

        proto.setUser("principal");
        proto.setId(UUID.randomUUID());
        proto.setName("Scion");

        MudCharacter instance = proto.buildInstance();

        instance.setRoomId(100L);

        assertTrue(proto.isPrototype());
        assertThrows(IllegalStateException.class, proto::getRoomId);

        assertFalse(instance.isPrototype());
        assertEquals(proto.getId(), instance.getId());
        assertEquals(100L, instance.getRoomId());
        assertEquals(proto.getName(), instance.getName());
        assertEquals(proto.getUser(), instance.getUser());
        assertThrows(IllegalStateException.class, instance::buildInstance);
    }

    @Test
    void testId() {
        MudCharacter uut = new MudCharacter();
        UUID id = UUID.randomUUID();

        uut.setId(id);

        assertEquals(id, uut.getId());
    }

    @Test
    void testUser() {
        MudCharacter uut = new MudCharacter();
        MudCharacter uutInst;
        String user = "user";

        uut.setUser(user);
        uutInst = uut.buildInstance();

        assertEquals(user, uut.getUser());
        assertEquals(user, uutInst.getUser());
    }

    @Test
    void testWebSocketSession() {
        MudCharacter uut = new MudCharacter();
        MudCharacter uutInst;
        String user = UUID.randomUUID().toString();
        String webSocketSession = "webSocketSession";

        uut.setUser(user);
        uutInst = uut.buildInstance();

        uutInst.setRoomId(100L);
        uutInst.setWebSocketSession(webSocketSession);

        assertThrows(IllegalStateException.class, uut::getWebSocketSession);
        assertEquals(webSocketSession, uutInst.getWebSocketSession());
    }

    @Test
    void testZoneId() {
        MudCharacter uut = new MudCharacter();
        MudCharacter uutInst;
        Long roomId = 100L;

        uutInst = uut.buildInstance();
        uutInst.setRoomId(roomId);

        assertThrows(IllegalStateException.class, uut::getZoneId);
        assertEquals(1L, uutInst.getZoneId());
    }

    @Test
    void testRoomId() {
        MudCharacter uut = new MudCharacter();
        MudCharacter uutInst;
        Long roomId = 100L;

        uutInst = uut.buildInstance();
        uutInst.setRoomId(roomId);

        assertThrows(IllegalStateException.class, uut::getRoomId);
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

        assertEquals(3, uut.getBaseStat(Stat.CON));
        assertEquals(3, uut.getDefense());
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
