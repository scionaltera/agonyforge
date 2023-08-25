package com.agonyforge.mud.demo.model.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
public class MudPropertyTest {
    @Test
    void testName() {
        MudProperty uut = new MudProperty();
        String name = "start.room";

        uut.setName(name);

        assertEquals(name, uut.getName());
    }

    @Test
    void testValue() {
        MudProperty uut = new MudProperty();
        String value = UUID.randomUUID().toString();

        uut.setValue(value);

        assertEquals(value, uut.getValue());
    }
}
