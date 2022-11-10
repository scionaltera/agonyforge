package com.agonyforge.mud.core.cli;

import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.session.Session;

import java.security.Principal;

public abstract class AbstractQuestion implements Question, BeanNameAware {
    private String beanName;

    @Override
    public abstract Output prompt(Principal principal, Session httpSession);

    @Override
    public abstract Response answer(Principal principal, Session httpSession, Input input);

    @Override
    public String getBeanName() {
        return beanName;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }
}
