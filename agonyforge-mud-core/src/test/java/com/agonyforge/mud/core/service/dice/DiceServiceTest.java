package com.agonyforge.mud.core.service.dice;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DiceServiceTest {
    @Test
    void testRoll() {
        DiceService uut = new DiceService();

        DiceResult result = uut.roll(3, 6);
        List<Integer> rolls = result.getRolls();

        assertEquals(6, result.getSize());
        assertEquals(3, rolls.size());
    }

    @Test
    void testRollModifier() {
        DiceService uut = new DiceService();

        DiceResult result = uut.roll(3, 6, 4);
        List<Integer> rolls = result.getRolls();

        assertEquals(6, result.getSize());
        assertEquals(3, rolls.size());
    }
}
