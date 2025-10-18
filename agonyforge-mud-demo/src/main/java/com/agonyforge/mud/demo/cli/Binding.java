package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.demo.cli.command.Command;
import com.agonyforge.mud.demo.model.constant.AdminFlag;
import com.agonyforge.mud.demo.model.impl.*;

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

    public CommandReference asCommandReference() {
        return (CommandReference) value;
    }

    public String asString() {
        return value.toString();
    }

    public Long asNumber() {
        return (Long) value;
    }

    public MudCharacter asCharacter() {
        return (MudCharacter) value;
    }

    public MudCharacterTemplate asCharacterTemplate() {
        return (MudCharacterTemplate) value;
    }

    public MudItem asItem() {
        return (MudItem) value;
    }

    public MudItemTemplate asItemTemplate() {
        return (MudItemTemplate) value;
    }

    public MudRoom asRoom() {
        return (MudRoom) value;
    }

    public AdminFlag asAdminFlag() {
        return (AdminFlag) value;
    }
}
