package com.agonyforge.mud.core.cli.menu.impl;

import com.agonyforge.mud.core.cli.menu.AbstractMenuItem;
import com.agonyforge.mud.core.cli.Color;
import com.agonyforge.mud.core.web.model.Output;

import static com.agonyforge.mud.core.cli.Color.GREEN;
import static com.agonyforge.mud.core.cli.Color.YELLOW;

public class MenuItem extends AbstractMenuItem {
    private Object item;

    public MenuItem(String key, String description) {
        super(key, description);
    }

    public MenuItem(String key, String description, Object item) {
        super(key, description);
        this.item = item;
    }

    @Override
    public Output render(Color... colors) {
        Output menu = new Output();
        Color primary = colors.length > 0 ? colors[0] : YELLOW;
        Color secondary = colors.length > 1 ? colors[1] : GREEN;

        menu.append(primary + getKey() + secondary + ") " + primary + getDescription());

        return menu;
    }

    public Object getItem() {
        return item;
    }
}
