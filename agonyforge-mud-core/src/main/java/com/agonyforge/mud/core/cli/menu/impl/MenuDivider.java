package com.agonyforge.mud.core.cli.menu.impl;

import com.agonyforge.mud.core.cli.menu.AbstractMenuDivider;
import com.agonyforge.mud.core.cli.Color;
import com.agonyforge.mud.core.web.model.Output;

import static com.agonyforge.mud.core.cli.Color.GREEN;

public class MenuDivider extends AbstractMenuDivider {
    public MenuDivider(int length) {
        super(length);
    }

    @Override
    public Output render(Color... colors) {
        Output menu = new Output();
        Color secondary = colors.length > 1 ? colors[1] : GREEN;

        menu.append(secondary + "*".repeat(getLength()));

        return menu;
    }
}
