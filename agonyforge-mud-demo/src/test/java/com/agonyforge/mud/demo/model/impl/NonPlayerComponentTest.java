package com.agonyforge.mud.demo.model.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class NonPlayerComponentTest {
    private static final Random RANDOM = new Random();

    @Test
    void testId() {
        Long id = RANDOM.nextLong();
        NonPlayerComponent uut = new NonPlayerComponent();

        uut.setId(id);

        assertEquals(id, uut.getId());
    }
}
