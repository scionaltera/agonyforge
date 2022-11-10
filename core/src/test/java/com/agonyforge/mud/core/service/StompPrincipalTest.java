package com.agonyforge.mud.core.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class StompPrincipalTest {
    @Test
    void testGetName() {
        Principal principal = new StompPrincipal("Jake");
        assertEquals("Jake", principal.getName());
    }
}
