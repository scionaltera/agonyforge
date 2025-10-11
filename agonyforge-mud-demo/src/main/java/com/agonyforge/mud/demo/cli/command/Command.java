package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.Binding;

import java.util.List;

public interface Command {
    default Question executeBinding(Question question, WebSocketContext webSocketContext, List<Binding> bindings, Output output) {
        throw new UnsupportedOperationException("Command does not yet support binding execution.");
    }

    Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Output output);

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
