package com.agonyforge.mud.core.cli;

import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;

import java.security.Principal;
import java.util.Map;

public interface Question {
    Output prompt(Principal principal, Map<String, Object> stompSession);
    Response answer(Principal principal, Map<String, Object> stompSession, Input input);
    String getBeanName();
}
