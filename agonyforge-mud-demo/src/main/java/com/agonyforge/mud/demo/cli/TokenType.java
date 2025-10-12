package com.agonyforge.mud.demo.cli;

public enum TokenType {
    WORD("a word", false, false),
    QUOTED_WORDS("multiple words", true, false),
    NUMBER("a number", false, true),
    DIRECTION("a direction", false, false),
    ITEM_GROUND("item on ground", false, false),
    ITEM_HELD("held item", false, false),
    ITEM_WORN("worn item", false, false),
    CHARACTER_IN_ROOM("character in room", false, false),
    CHARACTER_IN_ZONE("character nearby", false, false),
    CHARACTER_IN_WORLD("character anywhere", false, false),
    PLAYER_IN_ROOM("player in room", false, false),
    PLAYER_IN_ZONE("player nearby", false, false),
    PLAYER_IN_WORLD("player anywhere", false, false),
    NPC_IN_ROOM("NPC in room", false, false),
    NPC_IN_ZONE("NPC nearby", false, false),
    NPC_IN_WORLD("NPC anywhere", false, false),
    ITEM_ID("item ID", false, true),
    NPC_ID("NPC ID", false, true),
    ROOM_ID("room ID", false, true),
    COMMAND("command", false, false),
    ADMIN_FLAG("admin flag", false, false),
    STAT("stat", false, false),
    EFFORT("effort", false, false);

    private final String readable;
    private final boolean isQuoting;
    private final boolean isNumber;

    TokenType(String readable, boolean isQuoting, boolean isNumber) {
        this.readable = readable;
        this.isQuoting = isQuoting;
        this.isNumber = isNumber;
    }

    public String getReadable() {
        return readable;
    }

    public boolean isQuoting() {
        return isQuoting;
    }

    public boolean isNumber() {
        return isNumber;
    }

    @Override
    public String toString() {
        return "<" + getReadable() + ">";
    }
}
