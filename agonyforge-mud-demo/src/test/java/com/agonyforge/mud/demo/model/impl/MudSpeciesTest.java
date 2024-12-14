package com.agonyforge.mud.demo.model.impl;

import com.agonyforge.mud.demo.model.constant.Effort;
import com.agonyforge.mud.demo.model.constant.Stat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MudSpeciesTest {
    private final Random random = new Random();

    @Test
    void testId() {
        MudSpecies uut = new MudSpecies();
        Long id = random.nextLong();

        uut.setId(id);

        assertEquals(id, uut.getId());
    }

    @Test
    void testName() {
        MudSpecies uut = new MudSpecies();
        String name = "Species";

        uut.setName(name);

        assertEquals(name, uut.getName());
    }

    @Test
    void testEffort() {
        MudSpecies uut = new MudSpecies();

        uut.setEffort(Effort.BASIC, 2);

        assertEquals(2, uut.getEffort(Effort.BASIC));

        uut.addEffort(Effort.BASIC, 1);

        assertEquals(3, uut.getEffort(Effort.BASIC));
    }

    @Test
    void testStat() {
        MudSpecies uut = new MudSpecies();

        uut.setStat(Stat.CON, 2);

        assertEquals(2, uut.getStat(Stat.CON));

        uut.addStat(Stat.CON, 1);

        assertEquals(3, uut.getStat(Stat.CON));
    }
}
