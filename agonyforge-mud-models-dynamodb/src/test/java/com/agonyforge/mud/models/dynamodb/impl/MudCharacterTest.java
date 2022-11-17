package com.agonyforge.mud.models.dynamodb.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MudCharacterTest {
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
