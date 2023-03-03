package com.agonyforge.mud.core.web.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OutputTest {
    @Test
    void testDefaultConstructor() {
        Output empty = new Output();

        assertEquals(Collections.emptyList(), empty.toList());
        assertEquals("", empty.toString());
    }

    @Test
    void testPreloadConstructorCollection() {
        Output full = new Output(Arrays.asList("one", "two", "three"));
        List<String> expected = Arrays.asList("one", "two", "three");

        assertEquals(expected, full.toList());
        assertEquals("one\ntwo\nthree", full.toString());
    }

    @Test
    void testAppendOutputsConstructor() {
        Output output = new Output("One");
        Output append1 = new Output("Two");
        Output append2 = new Output("Three");

        Output result = new Output(output, append1, append2);

        assertEquals(Arrays.asList("One", "Two", "Three"), result.getOutput());
    }

    @Test
    void testAppendChaining() {
        Output output = new Output();

        assertEquals("", output.toString());

        output
            .append("Output!")
            .append("Now!");

        assertEquals(Arrays.asList("Output!", "Now!"), output.getOutput());
        assertEquals("Output!\nNow!", output.toString());
    }

    @Test
    void testAppendCollection() {
        Output output = new Output();

        assertEquals("", output.toString());

        output
            .append(Collections.singletonList("Output!"))
            .append(Collections.singletonList("Now!"));

        assertEquals(Arrays.asList("Output!", "Now!"), output.getOutput());
        assertEquals("Output!\nNow!", output.toString());
    }

    @Test
    void testAppendOutputs() {
        Output output = new Output();
        Output arg1 = new Output("Output!");
        Output arg2 = new Output("Now!");

        Output result = output
            .append(arg1, arg2);

        assertEquals(output, result);
        assertEquals(Arrays.asList("Output!", "Now!"), output.getOutput());
        assertEquals("Output!\nNow!", output.toString());
    }

    @Test
    void testEquality() {
        Output one = new Output("Testing");
        Output two = new Output("Testing");

        assertEquals(one, two);
        assertEquals(one.hashCode(), two.hashCode());
    }
}
