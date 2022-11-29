package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;

import java.util.List;

public interface Command {
    Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output);
}
