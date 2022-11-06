package com.agonyforge.mud.cli.menu;

import com.agonyforge.mud.web.model.Output;

public abstract class AbstractMenuItem implements MenuComponent {
    private final String key;
    private final String description;

    public AbstractMenuItem(String key, String description) {
        this.key = key;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public abstract Output render(Color... colors);
}
