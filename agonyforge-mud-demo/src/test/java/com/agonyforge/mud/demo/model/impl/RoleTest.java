package com.agonyforge.mud.demo.model.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class RoleTest {
    private static final Random RANDOM = new Random();

    @Mock
    private CommandReference commandA, commandB, commandC;

    @Test
    void testId() {
        Long id = RANDOM.nextLong();
        Role uut = new Role();

        uut.setId(id);

        assertEquals(id, uut.getId());
    }

    @Test
    void testName() {
        String name = "Tester";
        Role uut = new Role();

        uut.setName(name);

        assertEquals(name, uut.getName());
    }

    @Test
    void testCommands() {
        Set<CommandReference> commands = Set.of(commandA, commandB, commandC);
        Role uut = new Role();

        uut.setCommands(commands);

        assertEquals(commands, uut.getCommands());
    }
}
