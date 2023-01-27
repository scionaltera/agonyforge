package com.agonyforge.mud.demo.cli.menu;

import com.agonyforge.mud.core.cli.menu.AbstractMenuTitle;
import com.agonyforge.mud.core.cli.Color;
import com.agonyforge.mud.core.web.model.Output;

import static com.agonyforge.mud.core.cli.Color.GREEN;
import static com.agonyforge.mud.core.cli.Color.YELLOW;

public class MenuTitle extends AbstractMenuTitle {
    public MenuTitle(String title) {
        super(title);
    }

    @Override
    public Output render(Color... colors) {
        Output menu = new Output();
        Color primary = colors.length > 0 ? colors[0] : YELLOW;
        Color secondary = colors.length > 1 ? colors[1] : GREEN;
        MenuDivider divider = new MenuDivider(getTitle().length() + 6);

        menu.append("");
        menu.append(divider.render(colors));
        menu.append(secondary + "* " + primary + " " + getTitle() + " " + secondary + " *");
        menu.append(divider.render(colors));

        return menu;
    }
}
