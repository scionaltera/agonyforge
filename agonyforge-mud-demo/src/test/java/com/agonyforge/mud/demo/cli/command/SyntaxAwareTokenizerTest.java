package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.demo.cli.TokenType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SyntaxAwareTokenizerTest {
    private final List<TokenType> basicSyntax = List.of(TokenType.WORD, TokenType.WORD);

    @Test
    void testTokenize() {
        List<String> expected = List.of("able", "baker", "charlie");
        String input = "able baker charlie";

        assertEquals(expected, SyntaxAwareTokenizer.tokenize(input, basicSyntax));
    }

    @Test
    void testTokenizeEmptyInput() {
        String input = "";
        List<String> expected = List.of();

        assertEquals(expected, SyntaxAwareTokenizer.tokenize(input, List.of()));
    }

    @Test
    void testTokenizeWhitespace() {
        String input = "  able baker charlie  ";
        List<String> expected = List.of("able", "baker", "charlie");

        assertEquals(expected, SyntaxAwareTokenizer.tokenize(input, basicSyntax));
    }

    @Test
    void testTokenizeMultipleSpaces() {
        String input = "able   baker   charlie";
        List<String> expected = List.of("able", "baker", "charlie");

        assertEquals(expected, SyntaxAwareTokenizer.tokenize(input, basicSyntax));
    }

    @Test
    void testTokenizeSpecialChars() {
        String input = "!@# Able BAKER Charlie !@#";
        List<String> expected = List.of("!@#", "Able", "BAKER", "Charlie", "!@#");

        assertEquals(expected, SyntaxAwareTokenizer.tokenize(input, List.of(TokenType.WORD, TokenType.WORD, TokenType.WORD, TokenType.WORD)));
    }

    @Test
    void testTokenizeColorCodes() {
        String input = "[red]able baker [blue]charlie";
        List<String> expected = List.of("[red]able", "baker", "[blue]charlie");

        assertEquals(expected, SyntaxAwareTokenizer.tokenize(input, basicSyntax));
    }

    @Test
    void testTokenizeQuoting() {
        String input = "say some words in an implicitly quoted string";
        List<String> expected = List.of("say", "some words in an implicitly quoted string");

        assertEquals(expected, SyntaxAwareTokenizer.tokenize(input, List.of(TokenType.QUOTED_WORDS)));
    }

    @Test
    void testTokenizeQuotingWithQuotes() {
        String input = "say some words in an \"implicitly quoted\" string";
        List<String> expected = List.of("say", "some words in an \"implicitly quoted\" string");

        assertEquals(expected, SyntaxAwareTokenizer.tokenize(input, List.of(TokenType.QUOTED_WORDS)));
    }

    @Test
    void testTokenizeQuotingWithStartWhitespace() {
        String input = "say     some implicitly quoted words with space at the end";
        List<String> expected = List.of("say", "    some implicitly quoted words with space at the end");

        assertEquals(expected, SyntaxAwareTokenizer.tokenize(input, List.of(TokenType.QUOTED_WORDS)));
    }

    @Test
    void testTokenizeQuotingWithEndWhitespace() {
        String input = "say some implicitly quoted words with space at the end    ";
        List<String> expected = List.of("say", "some implicitly quoted words with space at the end    ");

        assertEquals(expected, SyntaxAwareTokenizer.tokenize(input, List.of(TokenType.QUOTED_WORDS)));
    }

    @Test
    void testTokenizeNumber() {
        String input = "edit 9000";
        List<String> expected = List.of("edit", "9000");

        assertEquals(expected, SyntaxAwareTokenizer.tokenize(input, List.of(TokenType.NUMBER)));
    }

    @Test
    void testTokenizeNumberInvalid() {
        String input = "edit number";

        assertThrows(IllegalArgumentException.class, () -> SyntaxAwareTokenizer.tokenize(input, List.of(TokenType.NUMBER)));
    }

    @Test
    void testSyntaxMissingToken() {
        String input = "edit 9000";

        assertThrows(IllegalArgumentException.class, () -> SyntaxAwareTokenizer.tokenize(input, List.of(TokenType.NUMBER, TokenType.WORD)));
    }

    @Test
    void testSyntaxExtraToken() {
        String input = "edit 9000";

        assertThrows(IllegalArgumentException.class, () -> SyntaxAwareTokenizer.tokenize(input, List.of()));
    }
}
