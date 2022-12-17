package com.agonyforge.mud.demo.cli;

public class CommandException extends RuntimeException {
    public CommandException() {
        super();
    }

    public CommandException(String message) {
        super(message);
    }
}
