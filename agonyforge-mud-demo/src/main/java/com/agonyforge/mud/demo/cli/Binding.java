package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.demo.cli.command.Command;
import com.agonyforge.mud.demo.model.impl.MudCharacter;

import java.util.Optional;

public class Binding {
    private final TokenType type;
    private final String token;
    private final Object value;

    public Binding(TokenType type, String token, Object value) {
        this.type = type;
        this.token = token;
        this.value = value;
    }

    public TokenType getType() {
        return type;
    }

    public String getToken() {
        return token;
    }

    public Object asObject() {
        return value;
    }

    public Command asCommand() {
        return (Command) value;
    }

    public String asString() {
        return value.toString();
    }

    public Integer asNumber() {
        return (Integer) value;
    }

    public MudCharacter asCharacter() {
        return (MudCharacter) value;
    }
}
