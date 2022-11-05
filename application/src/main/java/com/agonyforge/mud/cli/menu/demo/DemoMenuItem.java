package com.agonyforge.mud.cli.menu.demo;

import com.agonyforge.mud.cli.menu.AbstractMenuItem;
import com.agonyforge.mud.cli.menu.Color;
import com.agonyforge.mud.web.model.Output;

import static com.agonyforge.mud.cli.menu.Color.GREEN;
import static com.agonyforge.mud.cli.menu.Color.YELLOW;

public class DemoMenuItem extends AbstractMenuItem {
    public DemoMenuItem(String key, String description) {
        super(key, description);
    }

    @Override
    public Output render(Color... colors) {
        Output menu = new Output();
        Color primary = colors.length > 0 ? colors[0] : YELLOW;
        Color secondary = colors.length > 1 ? colors[1] : GREEN;

        menu.append(primary + getKey() + secondary + ") " + primary + getDescription());

        return menu;
    }
}
