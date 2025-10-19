package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.demo.cli.command.Command;
import com.agonyforge.mud.demo.model.impl.CommandReference;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class BindingTest {

    @Test
    public void testConstructor() {
        TokenType type = TokenType.WORD;
        String token = "token";
        String value = "test";
        Binding binding = new Binding(type, token, value);
        assertEquals(type, binding.getType());
        assertEquals(token, binding.getToken());
        assertEquals(value, binding.asObject());
    }

    @Test
    public void testAsCommandReference() {
        Command mockCommand = mock(Command.class);
        Binding binding = new Binding(TokenType.COMMAND, "mock", mockCommand);
        CommandReference result = binding.asCommandReference();
        assertNotNull(result);
        assertEquals(mockCommand, result);

        Binding nonCommandBinding = new Binding(TokenType.WORD, "test","test");
        assertThrows(ClassCastException.class, nonCommandBinding::asCommandReference);
    }

    @Test
    public void testAsString() {
        Binding wordBinding = new Binding(TokenType.WORD, "hello", "hello");
        assertEquals("hello", wordBinding.asString());

        Binding numberBinding = new Binding(TokenType.NUMBER, "42",42L);
        assertEquals("42", numberBinding.asString());
    }

    @Test
    public void testAsNumber() {
        Binding numberBinding = new Binding(TokenType.NUMBER, "42",42L);
        assertEquals(42L, numberBinding.asNumber());

        Binding nonNumberBinding = new Binding(TokenType.WORD, "test","test");
        assertThrows(ClassCastException.class, nonNumberBinding::asNumber);
    }

    @Test
    public void testAsCharacter() {
        MudCharacter stu = mock(MudCharacter.class);
        Binding characterBinding = new Binding(TokenType.CHARACTER_IN_ROOM, "stu", stu);
        assertEquals(stu, characterBinding.asCharacter());

        Binding nonCharacterBinding = new Binding(TokenType.WORD, "test","test");
        assertThrows(ClassCastException.class, nonCharacterBinding::asCharacter);
    }
}
