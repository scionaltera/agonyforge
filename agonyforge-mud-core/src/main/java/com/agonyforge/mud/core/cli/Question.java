package com.agonyforge.mud.core.cli;

import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;

public interface Question {
    Output prompt(WebSocketContext wsContext);
    Response answer(WebSocketContext wsContext, Input input);
    String getBeanName();
}
