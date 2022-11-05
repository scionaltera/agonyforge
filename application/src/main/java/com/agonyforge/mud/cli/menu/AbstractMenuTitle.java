package com.agonyforge.mud.cli.menu;

import com.agonyforge.mud.web.model.Output;

public abstract class AbstractMenuTitle implements MenuComponent {
    private String title;

    public AbstractMenuTitle(String title) {
        setTitle(title);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public abstract Output render(Color... colors);
}
