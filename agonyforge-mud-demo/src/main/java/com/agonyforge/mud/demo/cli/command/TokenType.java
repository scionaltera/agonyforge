package com.agonyforge.mud.demo.cli.command;

public enum TokenType {
    WORD(false, false),
    QUOTED_WORDS(true, false),
    NUMBER(false, true),
    DIRECTION(false, false),
    ITEM_GROUND(false, false),
    ITEM_HELD(false, false),
    ITEM_WORN(false, false),
    CHARACTER_IN_ROOM(false, false),
    CHARACTER_IN_ZONE(false, false),
    CHARACTER_IN_WORLD(false, false),
    ITEM_ID(false, true),
    NPC_ID(false, true),
    ROOM_ID(false, true),
    COMMAND(false, false),
    ADMIN_FLAG(false, false),
    STAT(false, false),
    EFFORT(false, false);

    private final boolean isQuoting;
    private final boolean isNumber;

    TokenType(boolean isQuoting, boolean isNumber) {
        this.isQuoting = isQuoting;
        this.isNumber = isNumber;
    }

    public boolean isQuoting() {
        return isQuoting;
    }

    public boolean isNumber() {
        return isNumber;
    }
}
