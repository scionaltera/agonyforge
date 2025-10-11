package com.agonyforge.mud.demo.cli.command;

import java.util.Optional;

public class Binding {
    private final TokenType type;
    private final Object value;

    public Binding(TokenType type, Object value) {
        this.type = type;
        this.value = value;
    }

    public TokenType getType() {
        return type;
    }

    public Object asObject() {
        return value;
    }

    public Optional<Command> asCommand() {
        if (TokenType.COMMAND == type) {
            return Optional.of((Command) value);
        }

        return Optional.empty();
    }

    public String asString() {
        if (TokenType.WORD == type || TokenType.QUOTED_WORDS == type) {
            return (String) value;
        } else {
            return value.toString();
        }
    }

    public Optional<Integer> asNumber() {
        if (TokenType.NUMBER == type) {
            return Optional.of((int) value);
        } else {
            return Optional.empty();
        }
    }
}
