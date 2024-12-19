package com.agonyforge.mud.demo.model.impl;

import com.agonyforge.mud.demo.model.constant.WearSlot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class MudItemTest {
    private final Random random = new Random();

    @Test
    void testBuildInstance() {
        MudItemPrototype proto = new MudItemPrototype();

        proto.setId(random.nextLong());
        proto.setNameList(List.of("sword"));
        proto.setShortDescription("a sword");
        proto.setLongDescription("A sword has been dropped on the ground here.");

        MudItem instance = proto.buildInstance();

        instance.setRoomId(100L);

        assertEquals(proto.getId(), instance.getId());
        assertEquals(100L, instance.getRoomId());
        assertEquals(proto.getNameList(), instance.getNameList());
        assertEquals(proto.getShortDescription(), instance.getShortDescription());
        assertEquals(proto.getLongDescription(), instance.getLongDescription());
    }

    @Test
    void testId() {
        MudItem uut = new MudItem();
        Long id = random.nextLong();

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

        uut.setWearSlots(EnumSet.of(WearSlot.HEAD));

        assertEquals(1, uut.getWearSlots().size());
        assertTrue(uut.getWearSlots().contains(WearSlot.HEAD));
    }

    @Test
    void testWorn() {
        MudItem uut = new MudItem();

        uut.setWorn(WearSlot.HEAD);

        assertEquals(WearSlot.HEAD, uut.getWorn());
    }

    @Test
    void testRoomId() {
        MudItemPrototype uut = new MudItemPrototype();
        MudItem uutInstance;
        Long roomId = 100L;

        uutInstance = uut.buildInstance();
        uutInstance.setRoomId(roomId);

        assertEquals(roomId, uutInstance.getRoomId());
        assertNull(uutInstance.getCharacterId());
    }

    @Test
    void testCharacterId() {
        MudItemPrototype uut = new MudItemPrototype();
        MudItem uutInstance;
        Long chId = random.nextLong();

        uutInstance = uut.buildInstance();
        uutInstance.setCharacterId(chId);

        assertEquals(chId, uutInstance.getCharacterId());
        assertNull(uutInstance.getRoomId());
    }
}
