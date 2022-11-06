package com.agonyforge.mud.demo.cli.menu;

import com.agonyforge.mud.core.cli.menu.AbstractMenuTitle;
import com.agonyforge.mud.core.cli.menu.Color;
import com.agonyforge.mud.core.web.model.Output;

import static com.agonyforge.mud.core.cli.menu.Color.GREEN;
import static com.agonyforge.mud.core.cli.menu.Color.YELLOW;

public class DemoMenuTitle extends AbstractMenuTitle {
    public DemoMenuTitle(String title) {
        super(title);
    }

    @Override
    public Output render(Color... colors) {
        Output menu = new Output();
        Color primary = colors.length > 0 ? colors[0] : YELLOW;
        Color secondary = colors.length > 1 ? colors[1] : GREEN;
        DemoMenuDivider divider = new DemoMenuDivider(getTitle().length() + 4);

        int padding = getTitle().length() / 2; // naive attempt to center it
        menu.append(divider.render(colors));
        menu.append(secondary + "* " + primary + " ".repeat(padding) + getTitle() + " ".repeat(padding) + secondary + " *");
        menu.append(divider.render(colors));

        return menu;
    }
}
