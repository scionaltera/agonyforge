package com.agonyforge.mud.demo.cli.menu;

import com.agonyforge.mud.core.cli.menu.AbstractMenuDivider;
import com.agonyforge.mud.core.cli.menu.Color;
import com.agonyforge.mud.core.web.model.Output;

import static com.agonyforge.mud.core.cli.menu.Color.GREEN;

public class DemoMenuDivider extends AbstractMenuDivider {
    public DemoMenuDivider(int length) {
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
