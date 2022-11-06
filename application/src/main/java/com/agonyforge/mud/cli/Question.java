package com.agonyforge.mud.cli;

import com.agonyforge.mud.web.model.Input;
import com.agonyforge.mud.web.model.Output;

import java.security.Principal;

public interface Question {
    Output prompt(Principal principal);
    Response answer(Principal principal, Input input);
}
