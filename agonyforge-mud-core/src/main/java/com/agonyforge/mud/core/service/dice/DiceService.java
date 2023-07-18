package com.agonyforge.mud.core.service.dice;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class DiceService {
    private static final Random random = new Random();

    public DiceResult roll(int count, int sides, int modifier) {
        DiceResult result = new DiceResult(sides, modifier);

        for (int i = 0; i < count; i++) {
            int roll = random.nextInt(1, sides + 1);
            result.addRoll(roll);
        }

        return result;
    }

    public DiceResult roll(int count, int sides) {
        return roll(count, sides, 0);
    }
}
