package com.agonyforge.mud.web.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InputTest {
    @Test
    void testSetInputConstructor() {
        Input input = new Input("Testing");

        assertEquals("Testing", input.getInput());
        assertEquals("Testing", input.toString());
    }

    @Test
    void testSetInputSetter() {
        Input input = new Input();
        input.setInput("Testing");

        assertEquals("Testing", input.getInput());
        assertEquals("Testing", input.toString());
    }

    @Test
    void setEquality() {
        Input input = new Input();
        Input match = new Input();

        input.setInput("Testing");
        match.setInput("Testing");

        assertEquals(match, input);
        assertEquals(match.hashCode(), input.hashCode());
    }
}
