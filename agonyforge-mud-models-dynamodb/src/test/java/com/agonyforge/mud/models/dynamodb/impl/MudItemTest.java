package com.agonyforge.mud.models.dynamodb.impl;

import com.agonyforge.mud.models.dynamodb.constant.WearSlot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class MudItemTest {
    @Test
    void testBuildInstance() {
        MudItem proto = new MudItem();

        proto.setId(UUID.randomUUID());
        proto.setNameList(List.of("sword"));
        proto.setShortDescription("a sword");
        proto.setLongDescription("A sword has been dropped on the ground here.");

        MudItem instance = proto.buildInstance();

        instance.setRoomId(100L);

        assertTrue(proto.isPrototype());
        assertThrows(IllegalStateException.class, proto::getRoomId);

        assertFalse(instance.isPrototype());
        assertEquals(proto.getId(), instance.getId());
        assertEquals(100L, instance.getRoomId());
        assertEquals(proto.getNameList(), instance.getNameList());
        assertEquals(proto.getShortDescription(), instance.getShortDescription());
        assertEquals(proto.getLongDescription(), instance.getLongDescription());
        assertThrows(IllegalStateException.class, instance::buildInstance);
    }

    @Test
    void testId() {
        MudItem uut = new MudItem();
        UUID id = UUID.randomUUID();

        uut.setId(id);

        assertEquals(id, uut.getId());
    }

    @Test
    void testNameList() {
        MudItem uut = new MudItem();
        List<String> nameList = List.of("sword");

        uut.setNameList(nameList);

        assertEquals(nameList, uut.getNameList());
    }

    @Test
    void testDescription() {
        MudItem uut = new MudItem();
        String shortDescription = "a sword";
        String longDescription = "A sword has been dropped on the ground here.";

        uut.setShortDescription(shortDescription);
        uut.setLongDescription(longDescription);

        assertEquals(shortDescription, uut.getShortDescription());
        assertEquals(longDescription, uut.getLongDescription());
    }

    @Test
    void testWearSlots() {
        MudItem uut = new MudItem();

        uut.setWearSlots(List.of(WearSlot.HEAD));

        assertEquals(1, uut.getWearSlots().size());
        assertEquals(WearSlot.HEAD, uut.getWearSlots().get(0));
    }

    @Test
    void testWorn() {
        MudItem uut = new MudItem();

        uut.setWorn(WearSlot.HEAD);

        assertEquals(WearSlot.HEAD, uut.getWorn());
    }

    @Test
    void testRoomId() {
        MudItem uut = new MudItem();
        MudItem uutInstance;
        Long roomId = 100L;

        uutInstance = uut.buildInstance();
        uutInstance.setRoomId(roomId);

        assertThrows(IllegalStateException.class, uut::getRoomId);
        assertEquals(roomId, uutInstance.getRoomId());
        assertNull(uutInstance.getCharacterId());
    }

    @Test
    void testCharacterId() {
        MudItem uut = new MudItem();
        MudItem uutInstance;
        UUID chId = UUID.randomUUID();

        uutInstance = uut.buildInstance();
        uutInstance.setCharacterId(chId);

        assertThrows(IllegalStateException.class, uut::getCharacterId);
        assertEquals(chId, uutInstance.getCharacterId());
        assertNull(uutInstance.getRoomId());
    }
}
