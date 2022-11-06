package com.agonyforge.mud.core.cli.menu;

import com.agonyforge.mud.core.web.model.Output;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMenuPane implements MenuComponent {
    private AbstractMenuTitle title;
    private final List<AbstractMenuItem> items = new ArrayList<>();
    private AbstractMenuPrompt prompt;

    public List<AbstractMenuItem> getItems() {
        return items;
    }

    public AbstractMenuTitle getTitle() {
        return title;
    }

    public void setTitle(AbstractMenuTitle title) {
        this.title = title;
    }

    public AbstractMenuPrompt getPrompt() {
        return prompt;
    }

    public void setPrompt(AbstractMenuPrompt prompt) {
        this.prompt = prompt;
    }

    @Override
    public abstract Output render(Color... colors);
}
