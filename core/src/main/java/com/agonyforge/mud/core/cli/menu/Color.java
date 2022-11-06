package com.agonyforge.mud.core.cli.menu;

public enum Color {
    DEFAULT("default"),
    DBLACK("dblack"),
    BLACK("black"),
    DWHITE("dwhite"),
    WHITE("white"),
    DRED("dred"),
    RED("red"),
    DYELLOW("dyellow"),
    YELLOW("yellow"),
    DGREEN("dgreen"),
    GREEN("green"),
    DCYAN("dcyan"),
    CYAN("cyan"),
    DBLUE("dblue"),
    BLUE("blue"),
    DMAGENTA("dmagenta"),
    MAGENTA("magenta");

    private final String value;

    Color(String value) {
        this.value = value;
    }

    public String toString() {
        return "[" + value + "]";
    }
}
