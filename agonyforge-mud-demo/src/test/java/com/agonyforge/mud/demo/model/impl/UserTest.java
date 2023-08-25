package com.agonyforge.mud.demo.model.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class UserTest {
    @Test
    void testPrincipalName() {
        User uut = new User();
        String name = "principal";

        uut.setPrincipalName(name);

        assertEquals(name, uut.getPrincipalName());
    }

    @Test
    void testGivenName() {
        User uut = new User();
        String name = "givenName";

        uut.setGivenName(name);

        assertEquals(name, uut.getGivenName());
    }

    @Test
    void testEmail() {
        User uut = new User();
        String email = "e@mail.test";

        uut.setEmailAddress(email);

        assertEquals(email, uut.getEmailAddress());
    }
}
