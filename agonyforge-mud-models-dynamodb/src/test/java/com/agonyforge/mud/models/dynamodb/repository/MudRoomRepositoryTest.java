package com.agonyforge.mud.models.dynamodb.repository;

import com.agonyforge.mud.models.dynamodb.impl.MudRoom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class MudRoomRepositoryTest extends DynamoDbLocalInitializingTest {
    @Test
    void testGetById() {
        MudRoomRepository uut = new MudRoomRepository(dynamoDbClient, tableNames);
        MudRoom room = new MudRoom();

        room.setId(1L);
        room.setZoneId(1L);
        room.setName("Test Room");
        room.setDescription("This is a room.");

        uut.save(room);

        Optional<MudRoom> resultOptional = uut.getById(1L);
        MudRoom result = resultOptional.orElseThrow();

        assertEquals(room.getId(), result.getId());
        assertEquals(room.getZoneId(), result.getZoneId());
        assertEquals(room.getName(), result.getName());
        assertEquals(room.getDescription(), result.getDescription());
    }

    @Test
    void testGetByIdNotFound() {
        MudRoomRepository uut = new MudRoomRepository(dynamoDbClient, tableNames);
        Optional<MudRoom> resultOptional = uut.getById(1L);

        assertTrue(resultOptional.isEmpty());
    }
}
