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

        instance.setRoomId(1L);

        assertTrue(proto.isPrototype());
        assertThrows(IllegalStateException.class, proto::getRoomId);

        assertFalse(instance.isPrototype());
        assertEquals(proto.getId(), instance.getId());
        assertEquals(1L, instance.getRoomId());
        assertEquals(proto.getName(), instance.getName());
        assertEquals(proto.getUser(), instance.getUser());
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
        MudCharacter uutInst;
        String user = "user";

        uut.setUser(user);
        uutInst = uut.buildInstance();

        assertEquals(user, uut.getUser());
        assertEquals(user, uutInst.getUser());
    }

    @Test
    void testWebSocketSession() {
        MudCharacter uut = new MudCharacter();
        MudCharacter uutInst;
        String user = UUID.randomUUID().toString();
        String webSocketSession = "webSocketSession";

        uut.setUser(user);
        uutInst = uut.buildInstance();

        uutInst.setRoomId(1L);
        uutInst.setWebSocketSession(webSocketSession);

        assertThrows(IllegalStateException.class, uut::getWebSocketSession);
        assertEquals(webSocketSession, uutInst.getWebSocketSession());
    }

    @Test
    void testRoomId() {
        MudCharacter uut = new MudCharacter();
        MudCharacter uutInst;
        Long roomId = 1L;

        uutInst = uut.buildInstance();
        uutInst.setRoomId(roomId);

        assertThrows(IllegalStateException.class, uut::getRoomId);
        assertEquals(roomId, uutInst.getRoomId());
    }

    @Test
    void testName() {
        MudCharacter uut = new MudCharacter();
        String name = "name";

        uut.setName(name);

        assertEquals(name, uut.getName());
    }
}
