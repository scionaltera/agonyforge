package com.agonyforge.mud.cli;

import com.agonyforge.mud.web.model.Input;
import com.agonyforge.mud.web.model.Output;

public interface Question {
    Output prompt();
    Response answer(Input input);
}
