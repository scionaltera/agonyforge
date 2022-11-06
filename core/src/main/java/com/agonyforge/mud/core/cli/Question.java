package com.agonyforge.mud.core.cli;

import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;

import java.security.Principal;

public interface Question {
    Output prompt(Principal principal);
    Response answer(Principal principal, Input input);
}
