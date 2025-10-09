package com.agonyforge.mud.demo.cli.command;

public class Token {
    private final String token;
    private final TokenType type;

    private Object binding;

    public Token(String token, TokenType type) {
        this.token = token;
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public TokenType getType() {
        return type;
    }

    public Object getBinding() {
        return binding;
    }

    public void setBinding(Object binding) {
        this.binding = binding;
    }
}
