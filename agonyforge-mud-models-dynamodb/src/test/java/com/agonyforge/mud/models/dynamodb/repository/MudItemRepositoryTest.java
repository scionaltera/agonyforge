package com.agonyforge.mud.models.dynamodb.repository;

import com.agonyforge.mud.models.dynamodb.impl.MudItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class MudItemRepositoryTest extends DynamoDbLocalInitializingTest {
    @Test
    void testGetByIdPrototype() {
        MudItemRepository uut = new MudItemRepository(dynamoDbClient, tableNames);
        MudItem item = new MudItem();
        UUID uuid = UUID.randomUUID();

        item.setId(uuid);
        item.setNameList(List.of("sword"));
        item.setShortDescription("a sword");
        item.setLongDescription("A sword has been dropped on the ground here.");

        uut.save(item);

        Optional<MudItem> resultOptional = uut.getById(uuid, true);
        MudItem result = resultOptional.orElseThrow();

        assertEquals(item.getId(), result.getId());
        assertThrows(IllegalStateException.class, result::getRoomId);
        assertThrows(IllegalStateException.class, result::getCharacterId);
        assertEquals(item.getNameList(), result.getNameList());
        assertEquals(item.getShortDescription(), result.getShortDescription());
        assertEquals(item.getLongDescription(), result.getLongDescription());
        assertTrue(item.isPrototype());
    }

    @Test
    void testGetByIdInstanceRoom() {
        MudItemRepository uut = new MudItemRepository(dynamoDbClient, tableNames);
        MudItem item = new MudItem();
        UUID uuid = UUID.randomUUID();

        item.setId(uuid);
        item.setNameList(List.of("sword"));
        item.setShortDescription("a sword");
        item.setLongDescription("A sword has been dropped on the ground here.");

        MudItem itemInstance = item.buildInstance();

        itemInstance.setRoomId(100L);

        uut.saveAll(List.of(item, itemInstance));

        Optional<MudItem> resultOptional = uut.getById(uuid, false);
        MudItem result = resultOptional.orElseThrow();

        assertEquals(itemInstance.getId(), result.getId());
        assertEquals(100L, result.getRoomId());
        assertNull(result.getCharacterId());
        assertEquals(itemInstance.getNameList(), result.getNameList());
        assertEquals(itemInstance.getShortDescription(), result.getShortDescription());
        assertEquals(itemInstance.getLongDescription(), result.getLongDescription());
        assertFalse(result.isPrototype());
    }

    @Test
    void testGetByIdInstanceCharacter() {
        MudItemRepository uut = new MudItemRepository(dynamoDbClient, tableNames);
        MudItem item = new MudItem();
        UUID uuid = UUID.randomUUID();

        item.setId(uuid);
        item.setNameList(List.of("sword"));
        item.setShortDescription("a sword");
        item.setLongDescription("A sword has been dropped on the ground here.");

        MudItem itemInstance = item.buildInstance();
        UUID chId = UUID.randomUUID();

        itemInstance.setCharacterId(chId);

        uut.saveAll(List.of(item, itemInstance));

        Optional<MudItem> resultOptional = uut.getById(uuid, false);
        MudItem result = resultOptional.orElseThrow();

        assertEquals(item.getId(), result.getId());
        assertNull(result.getRoomId());
        assertEquals(chId, result.getCharacterId());
        assertEquals(itemInstance.getNameList(), result.getNameList());
        assertEquals(itemInstance.getShortDescription(), result.getShortDescription());
        assertEquals(itemInstance.getLongDescription(), result.getLongDescription());
        assertFalse(result.isPrototype());
    }

    @Test
    void testGetByIdEmpty() {
        MudItemRepository uut = new MudItemRepository(dynamoDbClient, tableNames);
        UUID uuid = UUID.randomUUID();

        Optional<MudItem> resultOptional = uut.getById(uuid, true);
        assertTrue(resultOptional.isEmpty());
    }

    @Test
    void testGetByCharacter() {
        MudItemRepository uut = new MudItemRepository(dynamoDbClient, tableNames);
        MudItem item = new MudItem();
        UUID itemId = UUID.randomUUID();
        UUID chId = UUID.randomUUID();

        item.setId(itemId);
        item.setNameList(List.of("sword"));
        item.setShortDescription("a sword");
        item.setLongDescription("A sword has been dropped on the ground here.");

        MudItem itemInstance = item.buildInstance();

        itemInstance.setCharacterId(chId);

        uut.saveAll(List.of(item, itemInstance));

        List<MudItem> results = uut.getByCharacter(chId);
        MudItem result = results
            .stream()
            .filter(r -> itemId.equals(r.getId()))
            .findAny()
            .orElseThrow();

        assertEquals(itemInstance.getId(), result.getId());
        assertEquals(itemInstance.getNameList(), result.getNameList());
        assertEquals(itemInstance.getShortDescription(), result.getShortDescription());
        assertEquals(itemInstance.getLongDescription(), result.getLongDescription());
        assertEquals(itemInstance.getCharacterId(), result.getCharacterId());
        assertNull(result.getRoomId());
        assertFalse(result.isPrototype());
    }

    @Test
    void testGetByRoom() {
        MudItemRepository uut = new MudItemRepository(dynamoDbClient, tableNames);
        MudItem item = new MudItem();
        UUID itemId = UUID.randomUUID();
        Long roomId = 100L;

        item.setId(itemId);
        item.setNameList(List.of("sword"));
        item.setShortDescription("a sword");
        item.setLongDescription("A sword has been dropped on the ground here.");

        MudItem itemInstance = item.buildInstance();

        itemInstance.setRoomId(roomId);

        uut.saveAll(List.of(item, itemInstance));

        List<MudItem> results = uut.getByRoom(roomId);
        MudItem result = results
            .stream()
            .filter(r -> itemId.equals(r.getId()))
            .findAny()
            .orElseThrow();

        assertEquals(itemInstance.getId(), result.getId());
        assertEquals(itemInstance.getNameList(), result.getNameList());
        assertEquals(itemInstance.getShortDescription(), result.getShortDescription());
        assertEquals(itemInstance.getLongDescription(), result.getLongDescription());
        assertEquals(itemInstance.getRoomId(), result.getRoomId());
        assertNull(result.getCharacterId());
        assertFalse(result.isPrototype());
    }
}
