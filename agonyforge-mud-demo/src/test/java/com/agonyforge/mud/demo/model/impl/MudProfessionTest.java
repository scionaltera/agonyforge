package com.agonyforge.mud.demo.model.impl;

import com.agonyforge.mud.demo.model.constant.Effort;
import com.agonyforge.mud.demo.model.constant.Stat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MudProfessionTest {
    @Test
    void testId() {
        MudProfession uut = new MudProfession();
        UUID id = UUID.randomUUID();

        uut.setId(id);

        assertEquals(id, uut.getId());
    }

    @Test
    void testName() {
        MudProfession uut = new MudProfession();
        String name = "Profession";

        uut.setName(name);

        assertEquals(name, uut.getName());
    }

    @Test
    void testEffort() {
        MudProfession uut = new MudProfession();

        uut.setEffort(Effort.BASIC, 2);

        assertEquals(2, uut.getEffort(Effort.BASIC));

        uut.addEffort(Effort.BASIC, 1);

        assertEquals(3, uut.getEffort(Effort.BASIC));
    }

    @Test
    void testStat() {
        MudProfession uut = new MudProfession();

        uut.setStat(Stat.CON, 2);

        assertEquals(2, uut.getStat(Stat.CON));

        uut.addStat(Stat.CON, 1);

        assertEquals(3, uut.getStat(Stat.CON));
    }
}
