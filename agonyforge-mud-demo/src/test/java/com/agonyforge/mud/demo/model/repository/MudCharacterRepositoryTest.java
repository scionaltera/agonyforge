package com.agonyforge.mud.demo.model.repository;

import com.agonyforge.mud.demo.model.DynamoDbInitializer;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.repository.DynamoDbLocalInitializingTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.agonyforge.mud.demo.model.impl.ModelConstants.TYPE_PC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class MudCharacterRepositoryTest extends DynamoDbLocalInitializingTest {
    @BeforeAll
    static void setUpDatabase() throws Exception {
        new DynamoDbInitializer(getDynamoDbClient()).initialize();
    }

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

        chInstance.setRoomId(100L);
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

        chInstance.setRoomId(100L);
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
    void testGetByRoom() {
        MudCharacterRepository uut = new MudCharacterRepository(dynamoDbClient, tableNames);
        MudCharacter ch = new MudCharacter();
        MudCharacter ch2 = new MudCharacter();
        UUID uuid = UUID.randomUUID();
        String user = UUID.randomUUID().toString();
        UUID uuid2 = UUID.randomUUID();
        String user2 = UUID.randomUUID().toString();

        ch.setId(uuid);
        ch.setUser(user);
        ch.setName("Scion");

        ch2.setId(uuid2);
        ch2.setUser(user2);
        ch2.setName("Spook");

        MudCharacter chInstance1 = ch.buildInstance();

        chInstance1.setRoomId(100L);
        chInstance1.setWebSocketSession("webSocketSession1");

        MudCharacter chInstance2 = ch2.buildInstance();

        chInstance2.setRoomId(101L);
        chInstance2.setWebSocketSession("webSocketSession2");

        uut.saveAll(List.of(ch, ch2, chInstance1, chInstance2));

        List<MudCharacter> results = uut.getByRoom(100L);
        MudCharacter result = results.get(0);

        assertEquals(1, results.size());

        assertEquals(chInstance1.getId(), result.getId());
        assertEquals(chInstance1.getUser(), result.getUser());
        assertEquals(chInstance1.getName(), result.getName());
        assertEquals(chInstance1.isPrototype(), result.isPrototype());
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

        chInstance.setRoomId(100L);
        chInstance.setWebSocketSession("webSocketSession");

        uut.saveAll(List.of(ch, chInstance));

        List<MudCharacter> results = uut.getByType(TYPE_PC)
            .stream()
            .filter(pc -> uuid.equals(pc.getId())) // we'll get results from other tests too
            .toList();
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
