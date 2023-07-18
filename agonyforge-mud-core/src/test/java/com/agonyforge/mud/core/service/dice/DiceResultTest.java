package com.agonyforge.mud.core.service.dice;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DiceResultTest {
    @Test
    void testGetSides() {
        DiceResult uut = new DiceResult(6, 0);

        assertEquals(6, uut.getSize());
        assertEquals(0, uut.getModifier());
    }

    @Test
    void testIndividualRolls() {
        DiceResult uut = new DiceResult(6, 3);

        uut.addRoll(2);
        uut.addRoll(4);
        uut.addRoll(6);

        assertEquals(2, uut.getRoll(0));
        assertEquals(4, uut.getRoll(1));
        assertEquals(6, uut.getRoll(2));

        assertEquals(5, uut.getModifiedRoll(0));
        assertEquals(7, uut.getModifiedRoll(1));
        assertEquals(9, uut.getModifiedRoll(2));
    }

    @Test
    void testRoll() {
        DiceResult uut = new DiceResult(6, 0);

        uut.addRoll(6);
        uut.addRoll(3);

        List<Integer> results = uut.getRolls();

        assertEquals(2, results.size());
        assertEquals(0, uut.getModifier());
        assertEquals(6, results.get(0));
        assertEquals(3, results.get(1));
    }

    @Test
    void testModifiedRolls() {
        DiceResult uut = new DiceResult(6, 3);

        uut.addRoll(6);
        uut.addRoll(3);

        List<Integer> rawResults = uut.getRolls();
        List<Integer> modResults = uut.getModifiedRolls();

        assertEquals(rawResults.get(0) + uut.getModifier(), (int) modResults.get(0));
        assertEquals(rawResults.get(1) + uut.getModifier(), (int) modResults.get(1));
    }
}
