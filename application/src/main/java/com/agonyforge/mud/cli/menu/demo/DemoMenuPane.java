package com.agonyforge.mud.cli.menu.demo;

import com.agonyforge.mud.cli.menu.AbstractMenuPane;
import com.agonyforge.mud.cli.menu.Color;
import com.agonyforge.mud.web.model.Output;

import static com.agonyforge.mud.cli.menu.Color.GREEN;
import static com.agonyforge.mud.cli.menu.Color.YELLOW;

public class DemoMenuPane extends AbstractMenuPane {
    public DemoMenuPane(String title) {
        super(title);
    }

    @Override
    public Output render(Color... colors) {
        Output menu = new Output();
        Color primary = colors.length > 0 ? colors[0] : YELLOW;
        Color secondary = colors.length > 1 ? colors[1] : GREEN;
        DemoMenuDivider divider = new DemoMenuDivider(getTitle().length() + 4);

        // title bar
        int padding = getTitle().length() / 2; // naive attempt to center it
        menu.append(divider.render(colors));
        menu.append(secondary + "* " + primary + " ".repeat(padding) + getTitle() + " ".repeat(padding) + secondary + " *");
        menu.append(divider.render(colors));

        // options
        getItems().forEach(item -> menu.append(item.render(colors)));

        // prompt
        menu.append(getPrompt().render(colors));

        return menu;
    }
}
