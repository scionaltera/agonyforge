package com.agonyforge.mud.core.cli;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TokenizerTest {
    @Test
    void testTokenize() {
        List<String> expected = List.of("ABLE", "BAKER", "CHARLIE");
        String input = "able baker charlie";

        assertEquals(expected, Tokenizer.tokenize(input));
    }

    @Test
    void testTokenizeEmptyInput() {
        String input = "";
        List<String> expected = List.of();

        assertEquals(expected, Tokenizer.tokenize(input));
    }

    @Test
    void testTokenizeWhitespace() {
        String input = "  able baker charlie  ";
        List<String> expected = List.of("ABLE", "BAKER", "CHARLIE");

        assertEquals(expected, Tokenizer.tokenize(input));
    }

    @Test
    void testTokenizeMultipleSpaces() {
        String input = "able   baker   charlie";
        List<String> expected = List.of("ABLE", "BAKER", "CHARLIE");

        assertEquals(expected, Tokenizer.tokenize(input));
    }

    @Test
    void testTokenizeSpecialChars() {
        String input = "!@# Able BAKER Charlie !@#";
        List<String> expected = List.of("!@#", "ABLE", "BAKER", "CHARLIE", "!@#");

        assertEquals(expected, Tokenizer.tokenize(input));
    }

    @Test
    void testTokenizeColorCodes() {
        String input = "[red]able baker [blue]charlie";
        List<String> expected = List.of("ABLE", "BAKER", "CHARLIE");

        assertEquals(expected, Tokenizer.tokenize(input));
    }
}
