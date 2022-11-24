package com.agonyforge.mud.models.dynamodb.repository;

import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

        uut.saveAll(List.of(ch, chInstance));

        Optional<MudCharacter> resultOptional = uut.getById(uuid, false);
        MudCharacter result = resultOptional.orElseThrow();

        assertEquals(ch.getId(), result.getId());
        assertThrows(IllegalStateException.class, result::getUser);
        assertEquals(ch.getName(), result.getName());
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

        uut.saveAll(List.of(ch, chInstance));

        List<MudCharacter> results = uut.getByUser(user);
        MudCharacter result = results.get(0);

        assertEquals(1, results.size());

        assertEquals(ch.getId(), result.getId());
        assertEquals(ch.getUser(), result.getUser());
        assertEquals(ch.getName(), result.getName());
        assertEquals(ch.isPrototype(), result.isPrototype());
    }
}
