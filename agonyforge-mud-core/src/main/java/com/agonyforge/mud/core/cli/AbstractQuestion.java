package com.agonyforge.mud.core.cli;

import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import org.springframework.beans.factory.BeanNameAware;

public abstract class AbstractQuestion implements Question, BeanNameAware {
    private String beanName;

    @Override
    public abstract Output prompt(WebSocketContext wsContext);

    @Override
    public abstract Response answer(WebSocketContext webSocketContext, Input input);

    @Override
    public String getBeanName() {
        return beanName;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }
}
