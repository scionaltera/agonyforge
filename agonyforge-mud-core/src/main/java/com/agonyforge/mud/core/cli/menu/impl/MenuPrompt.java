package com.agonyforge.mud.core.cli.menu.impl;

import com.agonyforge.mud.core.cli.menu.AbstractMenuPrompt;
import com.agonyforge.mud.core.cli.Color;
import com.agonyforge.mud.core.web.model.Output;

import static com.agonyforge.mud.core.cli.Color.GREEN;
import static com.agonyforge.mud.core.cli.Color.YELLOW;

public class MenuPrompt extends AbstractMenuPrompt {
    @Override
    public Output render(Color... colors) {
        Output menu = new Output();
        Color primary = colors.length > 0 ? colors[0] : YELLOW;
        Color secondary = colors.length > 1 ? colors[1] : GREEN;

        menu
            .append("")
            .append(secondary + "Please " + primary + "make your selection" + secondary + ": ");

        return menu;
    }
}
