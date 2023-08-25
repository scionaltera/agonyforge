package com.agonyforge.mud.demo.model.constant;

public enum Direction {
    NORTH("north", "south"),
    EAST("east", "west"),
    SOUTH("south", "north"),
    WEST("west", "east"),
    UP("up", "down"),
    DOWN("down", "up");

    private final String name;
    private final String opposite;

    Direction(String name, String opposite) {
        this.name = name;
        this.opposite = opposite;
    }

    public String getName() {
        return name;
    }

    public String getOpposite() {
        return opposite;
    }
}
