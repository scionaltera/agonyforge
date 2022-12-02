package com.agonyforge.mud.models.dynamodb.impl;

import com.agonyforge.mud.models.dynamodb.constant.Direction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MudRoomTest {
    @Test
    void testId() {
        MudRoom uut = new MudRoom();
        Long id = 42L;

        uut.setId(id);

        assertEquals(id, uut.getId());
    }

    @Test
    void testZoneId() {
        MudRoom uut = new MudRoom();
        Long zoneId = 1L;

        uut.setZoneId(zoneId);

        assertEquals(zoneId, uut.getZoneId());
    }

    @Test
    void testName() {
        MudRoom uut = new MudRoom();
        String name = "Test Room";

        uut.setName(name);

        assertEquals(name, uut.getName());
    }

    @Test
    void testDescription() {
        MudRoom uut = new MudRoom();
        String description = "This is a room.";

        uut.setDescription(description);

        assertEquals(description, uut.getDescription());
    }

    @Test
    void testExitNames() {
        MudRoom uut = new MudRoom();

        uut.setExit(Direction.WEST.name(), new MudRoom.Exit(101L));

        assertEquals(1, uut.getExits().size());
        assertEquals(Direction.WEST.name(), uut.getExits().get(0));
    }

    @Test
    void testExits() {
        MudRoom uut = new MudRoom();

        uut.setExit(Direction.WEST.name(), new MudRoom.Exit(101L));

        assertEquals(101L, uut.getExit(Direction.WEST.name()).getDestinationId());
    }
}
