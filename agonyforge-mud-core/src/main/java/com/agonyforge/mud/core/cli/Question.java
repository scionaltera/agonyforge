package com.agonyforge.mud.core.cli;

import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import org.springframework.session.Session;

import java.security.Principal;

public interface Question {
    Output prompt(Principal principal, Session httpSession);
    Response answer(Principal principal, Session httpSession, Input input);
    String getBeanName();
}
