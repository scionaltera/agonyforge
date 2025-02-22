package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public interface Command {
    Logger LOGGER = LoggerFactory.getLogger(Command.class);

    Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output);

    static String stripFirstWord(String input) {
        int space = input.indexOf(' ');

        if (space == -1) {
            return "";
        }

        return input.substring(space + 1).stripLeading();
    }

    static String stripFirstWords(String input, int words) {
        for (int i = 0; i < words; i++) {
            input = stripFirstWord(input);
        }

        return input;
    }

    static String stripColors(String input) {
        boolean inColor = false;
        StringBuilder out = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '[') {
                inColor = true;
            }

            if (!inColor) {
                out.append(input.charAt(i));
            }

            if (input.charAt(i) == ']') {
                inColor = false;
            }
        }

        return out.toString();
    }
}
