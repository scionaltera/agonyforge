package com.agonyforge.mud.core.cli;

import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
    public static List<String> tokenize(String escaped) {
        List<String> tokens = new ArrayList<>();
        StringBuilder buf = new StringBuilder();
        boolean isQuoting = false;
        boolean isColor = false;

        String input = HtmlUtils.htmlUnescape(escaped).trim();

        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '"') {
                isQuoting = !isQuoting;
            } else if (input.charAt(i) == '[') {
                isColor = true;
            } else if (input.charAt(i) == ']') {
                isColor = false;
            } else if (isColor) {
                // do nothing
            } else if (!isQuoting && input.charAt(i) == ' ') {
                addTokenIfNotBlank(tokens, buf);
                buf.setLength(0);
            } else {
                buf.append(input.charAt(i));
            }
        }

        if (!buf.isEmpty()) {
            addTokenIfNotBlank(tokens, buf);
        }

        return tokens;
    }

    private static void addTokenIfNotBlank(List<String> tokens, StringBuilder buf) {
        String token = buf.toString().trim();

        if (!token.isBlank()) {
            tokens.add(token);
        }
    }
}
