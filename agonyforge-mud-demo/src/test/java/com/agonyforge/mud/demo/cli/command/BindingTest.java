package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.cli.TokenType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class BindingTest {

    @Test
    public void testConstructor() {
        TokenType type = TokenType.WORD;
        String value = "test";
        Binding binding = new Binding(type, value);
        assertEquals(type, binding.getType());
        assertEquals(value, binding.asObject());
    }

    @Test
    public void testAsCommand() {
        Command mockCommand = mock(Command.class);
        Binding binding = new Binding(TokenType.COMMAND, mockCommand);
        Optional<Command> result = binding.asCommand();
        assertTrue(result.isPresent());
        assertEquals(mockCommand, result.get());

        Binding nonCommandBinding = new Binding(TokenType.WORD, "test");
        assertFalse(nonCommandBinding.asCommand().isPresent());
    }

    @Test
    public void testAsString() {
        Binding wordBinding = new Binding(TokenType.WORD, "hello");
        assertEquals("hello", wordBinding.asString());

        Binding numberBinding = new Binding(TokenType.NUMBER, 42);
        assertEquals("42", numberBinding.asString());
    }

    @Test
    public void testAsNumber() {
        Binding numberBinding = new Binding(TokenType.NUMBER, 42);
        assertEquals(Optional.of(42), numberBinding.asNumber());

        Binding nonNumberBinding = new Binding(TokenType.WORD, "test");
        assertFalse(nonNumberBinding.asNumber().isPresent());
    }
}
