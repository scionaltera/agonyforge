package com.agonyforge.mud.demo.model.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MudZoneTest {
    @Test
    void testId() {
        MudZone uut = new MudZone();
        Long id = 42L;

        uut.setId(id);

        assertEquals(id, uut.getId());
    }

    @Test
    void testName() {
        MudZone uut = new MudZone();
        String name = "Test Zone";

        uut.setName(name);

        assertEquals(name, uut.getName());
    }
}
