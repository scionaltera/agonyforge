package com.agonyforge.mud.demo.model.impl;

import com.agonyforge.mud.demo.model.constant.WearSlot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.EnumSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class MudItemTest {
    private final Random random = new Random();

    @Test
    void testBuildInstance() {
        MudItemPrototype proto = new MudItemPrototype();
        proto.setItem(new ItemComponent());

        proto.setId(random.nextLong());
        proto.getItem().setNameList(Set.of("sword"));
        proto.getItem().setShortDescription("a sword");
        proto.getItem().setLongDescription("A sword has been dropped on the ground here.");

        MudItem instance = proto.buildInstance();

        instance.setRoomId(100L);

        assertEquals(proto.getId(), instance.getId());
        assertEquals(100L, instance.getRoomId());
        assertEquals(proto.getItem().getNameList(), instance.getItem().getNameList());
        assertEquals(proto.getItem().getShortDescription(), instance.getItem().getShortDescription());
        assertEquals(proto.getItem().getLongDescription(), instance.getItem().getLongDescription());
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
        uut.setItem(new ItemComponent());

        Set<String> nameList = Set.of("sword");
        uut.getItem().setNameList(nameList);

        assertEquals(nameList, uut.getItem().getNameList());
    }

    @Test
    void testDescription() {
        MudItem uut = new MudItem();
        uut.setItem(new ItemComponent());

        String shortDescription = "a sword";
        String longDescription = "A sword has been dropped on the ground here.";

        uut.getItem().setShortDescription(shortDescription);
        uut.getItem().setLongDescription(longDescription);

        assertEquals(shortDescription, uut.getItem().getShortDescription());
        assertEquals(longDescription, uut.getItem().getLongDescription());
    }

    @Test
    void testWearSlots() {
        MudItem uut = new MudItem();
        uut.setItem(new ItemComponent());

        uut.getItem().setWearSlots(EnumSet.of(WearSlot.HEAD));

        assertEquals(1, uut.getItem().getWearSlots().size());
        assertTrue(uut.getItem().getWearSlots().contains(WearSlot.HEAD));
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
        uut.setItem(new ItemComponent());

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
        uut.setItem(new ItemComponent());

        MudItem uutInstance;
        Long chId = random.nextLong();

        uutInstance = uut.buildInstance();
        uutInstance.setCharacterId(chId);

        assertEquals(chId, uutInstance.getCharacterId());
        assertNull(uutInstance.getRoomId());
    }
}
