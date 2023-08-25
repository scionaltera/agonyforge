package com.agonyforge.mud.demo.model.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class UserSessionTest {
    @Test
    void testPrincipalName() {
        UserSession uut = new UserSession();
        String name = "principal";

        uut.setPrincipalName(name);

        assertEquals(name, uut.getPrincipalName());
    }

    @Test
    void testRemoteIpAddress() {
        UserSession uut = new UserSession();
        String name = "999.888.777.666";

        uut.setRemoteIpAddress(name);

        assertEquals(name, uut.getRemoteIpAddress());
    }
}
