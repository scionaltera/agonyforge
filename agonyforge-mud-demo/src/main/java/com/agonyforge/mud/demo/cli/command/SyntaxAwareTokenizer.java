package com.agonyforge.mud.demo.cli.command;

import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;

import static com.agonyforge.mud.demo.cli.command.TokenType.COMMAND;

public class SyntaxAwareTokenizer {
    public static List<String> tokenize(String escaped, List<TokenType> syntax) throws IllegalArgumentException {
        if (escaped.isBlank()) {
            return List.of();
        }

        List<TokenType> defensiveSyntax = new ArrayList<>();

        defensiveSyntax.add(COMMAND);
        defensiveSyntax.addAll(syntax);

        List<String> tokens = new ArrayList<>();
        StringBuilder buf = new StringBuilder();
        TokenType currentType = defensiveSyntax.remove(0);
        boolean isQuoting = false;
        boolean isColor = false;

        String input = HtmlUtils.htmlUnescape(escaped);

        for (int i = 0; i < input.length(); i++) {
            if (currentType.isQuoting()) {
                isQuoting = true;
            }

            if (!currentType.isQuoting() && input.charAt(i) == '"') {
                isQuoting = !isQuoting;
            } else if (input.charAt(i) == '[') {
                isColor = true;
            } else if (input.charAt(i) == ']') {
                isColor = false;
            } else if (isColor) {
                // do nothing
            } else if (!isQuoting && input.charAt(i) == ' ') {
                if (validateAndAddToken(tokens, buf, currentType)) {
                    if (!defensiveSyntax.isEmpty()) {
                        currentType = defensiveSyntax.remove(0);
                    }
                }

                buf.setLength(0);
            } else {
                buf.append(input.charAt(i));
            }
        }

        if (!buf.isEmpty()) {
            validateAndAddToken(tokens, buf, currentType);
        }

        if (!defensiveSyntax.isEmpty()) {
            throw new IllegalArgumentException("Not enough tokens found.");
        }

        if (tokens.size() != 1 + syntax.size()) {
            throw new IllegalArgumentException("More tokens found than specified in the syntax.");
        }

        return tokens;
    }

    private static boolean validateAndAddToken(List<String> tokens, StringBuilder buf, TokenType type) throws IllegalArgumentException {
        String token = buf.toString();

        if (token.isBlank()) {
            return false;
        }

        if (type.isNumber()) {
            try {
                Long.parseLong(token);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Token was supposed to be a number.");
            }
        }

        tokens.add(token);
        return true;
    }
}
