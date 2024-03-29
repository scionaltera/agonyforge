package com.agonyforge.mud.demo.model.repository;

import com.agonyforge.mud.demo.model.DynamoDbInitializer;
import com.agonyforge.mud.demo.model.constant.Direction;
import com.agonyforge.mud.demo.model.impl.MudRoom;
import com.agonyforge.mud.models.dynamodb.repository.DynamoDbLocalInitializingTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class MudRoomRepositoryTest extends DynamoDbLocalInitializingTest {
    @BeforeAll
    static void setUpDatabase() throws Exception {
        new DynamoDbInitializer(getDynamoDbClient()).initialize();
    }

    @Test
    void testGetById() {
        MudRoomRepository uut = new MudRoomRepository(dynamoDbClient, tableNames);
        MudRoom room = new MudRoom();

        room.setId(100L);
        room.setZoneId(1L);
        room.setName("Test Room");
        room.setDescription("This is a room.");
        room.setExit(Direction.WEST.getName(), new MudRoom.Exit(101L));

        uut.save(room);

        Optional<MudRoom> resultOptional = uut.getById(100L);
        MudRoom result = resultOptional.orElseThrow();

        assertEquals(room.getId(), result.getId());
        assertEquals(room.getZoneId(), result.getZoneId());
        assertEquals(room.getName(), result.getName());
        assertEquals(room.getDescription(), result.getDescription());
        assertEquals(
            room.getExit(Direction.WEST.getName()).getDestinationId(),
            result.getExit(Direction.WEST.getName()).getDestinationId());
    }

    @Test
    void testGetByIdNotFound() {
        MudRoomRepository uut = new MudRoomRepository(dynamoDbClient, tableNames);
        Optional<MudRoom> resultOptional = uut.getById(100L);

        assertTrue(resultOptional.isEmpty());
    }
}
