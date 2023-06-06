package com.agonyforge.mud.models.dynamodb.constant;

public enum Effort {
    BASIC("Basic", 4),
    WEAPONS_N_TOOLS("Weapons & Tools", 6),
    GUNS("Guns", 8),
    ENERGY_N_MAGIC("Energy & Magic", 10),
    ULTIMATE("Ultimate", 12);

    private final String name;
    private final int die;

    Effort(String name, int die) {
        this.name = name;
        this.die = die;
    }

    public String getName() {
        return name;
    }

    public int getDie() {
        return die;
    }
}
