package com.agonyforge.mud.models.dynamodb.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

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
        proto.setName("sword");
        proto.setDescription("A sword.");

        MudItem instance = proto.buildInstance();

        instance.setRoomId(100L);

        assertTrue(proto.isPrototype());
        assertThrows(IllegalStateException.class, proto::getRoomId);

        assertFalse(instance.isPrototype());
        assertEquals(proto.getId(), instance.getId());
        assertEquals(100L, instance.getRoomId());
        assertEquals(proto.getName(), instance.getName());
        assertEquals(proto.getDescription(), instance.getDescription());
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
    void testName() {
        MudItem uut = new MudItem();
        String name = "sword";

        uut.setName(name);

        assertEquals(name, uut.getName());
    }

    @Test
    void testDescription() {
        MudItem uut = new MudItem();
        String description = "A sword.";

        uut.setDescription(description);

        assertEquals(description, uut.getDescription());
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
