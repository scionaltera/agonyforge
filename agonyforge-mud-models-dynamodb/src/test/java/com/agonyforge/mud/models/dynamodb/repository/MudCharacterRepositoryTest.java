package com.agonyforge.mud.models.dynamodb.repository;

import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.agonyforge.mud.models.dynamodb.impl.Constants.TYPE_PC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class MudCharacterRepositoryTest extends DynamoDbLocalInitializingTest {
    @Test
    void testGetByIdPrototype() {
        MudCharacterRepository uut = new MudCharacterRepository(dynamoDbClient, tableNames);
        MudCharacter ch = new MudCharacter();
        UUID uuid = UUID.randomUUID();
        String user = UUID.randomUUID().toString();

        ch.setId(uuid);
        ch.setUser(user);
        ch.setName("Scion");

        uut.save(ch);

        Optional<MudCharacter> resultOptional = uut.getById(uuid, true);
        MudCharacter result = resultOptional.orElseThrow();

        assertEquals(ch.getId(), result.getId());
        assertEquals(ch.getUser(), result.getUser());
        assertThrows(IllegalStateException.class, result::getRoomId);
        assertThrows(IllegalStateException.class, result::getWebSocketSession);
        assertEquals(ch.getName(), result.getName());
        assertEquals(ch.isPrototype(), result.isPrototype());
    }

    @Test
    void testGetByIdInstance() {
        MudCharacterRepository uut = new MudCharacterRepository(dynamoDbClient, tableNames);
        MudCharacter ch = new MudCharacter();
        UUID uuid = UUID.randomUUID();
        String user = UUID.randomUUID().toString();

        ch.setId(uuid);
        ch.setUser(user);
        ch.setName("Scion");

        MudCharacter chInstance = ch.buildInstance();

        chInstance.setRoomId(1L);
        chInstance.setWebSocketSession("webSocketSession");

        uut.saveAll(List.of(ch, chInstance));

        Optional<MudCharacter> resultOptional = uut.getById(uuid, false);
        MudCharacter result = resultOptional.orElseThrow();

        assertEquals(ch.getId(), result.getId());
        assertEquals(ch.getUser(), result.getUser());
        assertEquals(ch.getName(), result.getName());

        assertEquals(chInstance.getRoomId(), result.getRoomId());
        assertEquals(chInstance.getWebSocketSession(), result.getWebSocketSession());
        assertEquals(chInstance.isPrototype(), result.isPrototype());
    }

    @Test
    void testGetByIdEmpty() {
        MudCharacterRepository uut = new MudCharacterRepository(dynamoDbClient, tableNames);
        UUID uuid = UUID.randomUUID();

        Optional<MudCharacter> resultOptional = uut.getById(uuid, true);
        assertTrue(resultOptional.isEmpty());
    }

    @Test
    void testGetByUser() {
        MudCharacterRepository uut = new MudCharacterRepository(dynamoDbClient, tableNames);
        MudCharacter ch = new MudCharacter();
        UUID uuid = UUID.randomUUID();
        String user = UUID.randomUUID().toString();

        ch.setId(uuid);
        ch.setUser(user);
        ch.setName("Scion");

        MudCharacter chInstance = ch.buildInstance();

        chInstance.setRoomId(1L);
        chInstance.setWebSocketSession("webSocketSession");

        uut.saveAll(List.of(ch, chInstance));

        List<MudCharacter> results = uut.getByUser(user);
        MudCharacter result = results.get(0);

        assertEquals(1, results.size());

        assertEquals(ch.getId(), result.getId());
        assertEquals(ch.getUser(), result.getUser());
        assertEquals(ch.getName(), result.getName());
        assertEquals(ch.isPrototype(), result.isPrototype());
    }

    @Test
    void testGetByType() {
        MudCharacterRepository uut = new MudCharacterRepository(dynamoDbClient, tableNames);
        MudCharacter ch = new MudCharacter();
        UUID uuid = UUID.randomUUID();
        String user = UUID.randomUUID().toString();
        String name = "Scion";

        ch.setId(uuid);
        ch.setUser(user);
        ch.setName(name);

        MudCharacter chInstance = ch.buildInstance();

        chInstance.setRoomId(1L);
        chInstance.setWebSocketSession("webSocketSession");

        uut.saveAll(List.of(ch, chInstance));

        List<MudCharacter> results = uut.getByType(TYPE_PC)
            .stream()
            .filter(pc -> uuid.equals(pc.getId())) // we'll get results from other tests too
            .collect(Collectors.toList());
        MudCharacter result1 = results.get(0);
        MudCharacter result2 = results.get(1);

        assertEquals(ch.getId(), result1.getId());
        assertEquals(ch.getUser(), result1.getUser());
        assertEquals(ch.getName(), result1.getName());
        assertTrue(result1.isPrototype());

        assertEquals(ch.getId(), result2.getId());
        assertEquals(ch.getUser(), result2.getUser());
        assertEquals(ch.getName(), result2.getName());
        assertEquals(chInstance.getRoomId(), result2.getRoomId());
        assertEquals(chInstance.getWebSocketSession(), result2.getWebSocketSession());
        assertFalse(result2.isPrototype());
    }
}
