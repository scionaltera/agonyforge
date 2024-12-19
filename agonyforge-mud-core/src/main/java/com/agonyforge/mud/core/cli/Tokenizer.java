package com.agonyforge.mud.core.cli;

import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Tokenizer {
    public static List<String> tokenize(String escaped) {
        List<String> tokens = new ArrayList<>();
        StringBuilder buf = new StringBuilder();
        boolean isQuoting = false;

        String input = HtmlUtils.htmlUnescape(escaped).trim();

        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '"') {
                isQuoting = !isQuoting;
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
        String token = buf.toString().trim().toUpperCase(Locale.ROOT);

        if (!token.isBlank()) {
            tokens.add(token);
        }
    }
}
