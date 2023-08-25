package com.agonyforge.mud.demo.model.constant;

public enum Stat {
    STR("Strength", "STR"),
    DEX("Dexterity", "DEX"),
    CON("Constitution", "CON"),
    INT("Intelligence", "INT"),
    WIS("Wisdom", "WIS"),
    CHA("Charisma", "CHA");

    private final String name;
    private final String abbreviation;

    Stat(String name, String abbreviation) {
        this.name = name;
        this.abbreviation = abbreviation;
    }

    public String getName() {
        return name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }
}
