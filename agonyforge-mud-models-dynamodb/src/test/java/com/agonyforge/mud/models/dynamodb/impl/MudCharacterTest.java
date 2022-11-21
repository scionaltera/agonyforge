package com.agonyforge.mud.models.dynamodb.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class MudCharacterTest {
    @Test
    void testBuildInstance() {
        MudCharacter proto = new MudCharacter();

        proto.setUser("principal");
        proto.setId(UUID.randomUUID());
        proto.setName("Scion");

        MudCharacter instance = proto.buildInstance();

        assertTrue(proto.isPrototype());
        assertFalse(instance.isPrototype());

        assertThrows(IllegalStateException.class, instance::getUser);
        assertEquals(proto.getId(), instance.getId());
        assertEquals(proto.getName(), instance.getName());

        assertThrows(IllegalStateException.class, instance::buildInstance);
    }

    @Test
    void testId() {
        MudCharacter uut = new MudCharacter();
        UUID id = UUID.randomUUID();

        uut.setId(id);

        assertEquals(id, uut.getId());
    }

    @Test
    void testUser() {
        MudCharacter uut = new MudCharacter();
        String user = "user";

        uut.setUser(user);

        assertEquals(user, uut.getUser());
    }

    @Test
    void testName() {
        MudCharacter uut = new MudCharacter();
        String name = "name";

        uut.setName(name);

        assertEquals(name, uut.getName());
    }
}
