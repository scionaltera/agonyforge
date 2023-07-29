package com.agonyforge.mud.core.service.dice;

import java.util.ArrayList;
import java.util.List;

public class DiceResult {
    private final int size;
    private final int modifier;
    private final List<Integer> rolls = new ArrayList<>();

    DiceResult(int size, int modifier) {
        this.size = size;
        this.modifier = modifier;
    }

    /**
     * Add a roll to the DiceResult.
     *
     * @param result A result of rolling a single die.
     */
    void addRoll(int result) {
        rolls.add(result);
    }

    /**
     * Get the value of a specific roll without the modifier.
     *
     * @param i Which roll to get. Zero indexed.
     * @return The raw value of the requested roll.
     */
    public int getRoll(int i) {
        return rolls.get(i);
    }

    /**
     * Get the value of a specific roll with the modifier added.
     *
     * @param i Which roll to get. Zero indexed.
     * @return The modified value of the requested roll.
     */
    public int getModifiedRoll(int i) {
        return rolls.get(i) + modifier;
    }

    /**
     * Returns the array of dice rolls without any modifier.
     *
     * @return An array of Integers containing the results of the dice rolls.
     */
    public List<Integer> getRolls() {
        return new ArrayList<>(rolls);
    }

    /**
     * Returns the array of dice rolls with the modifier applied.
     *
     * @return An array of Integers containing the results of the dice rolls with the added modifier.
     */
    public List<Integer> getModifiedRolls() {
        return getRolls()
            .stream()
            .map(roll -> roll + getModifier())
            .toList();
    }

    /**
     * Indicates the size of the dice that were rolled.
     *
     * @return The number of faces on the dice that were rolled.
     */
    public int getSize() {
        return size;
    }

    /**
     * Indicates the modifier applied to the rolls.
     *
     * @return The modifier that was added to each of the rolls.
     */
    public int getModifier() {
        return modifier;
    }
}
