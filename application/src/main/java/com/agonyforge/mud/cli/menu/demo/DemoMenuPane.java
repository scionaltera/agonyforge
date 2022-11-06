package com.agonyforge.mud.cli.menu.demo;

import com.agonyforge.mud.cli.menu.AbstractMenuPane;
import com.agonyforge.mud.cli.menu.Color;
import com.agonyforge.mud.web.model.Output;

public class DemoMenuPane extends AbstractMenuPane {
    @Override
    public Output render(Color... colors) {
        Output menu = new Output();

        if (getTitle() != null) {
            menu.append(getTitle().render(colors));
        }

        getItems().forEach(item -> menu.append(item.render(colors)));

        if (getPrompt() != null) {
            menu.append(getPrompt().render(colors));
        }

        return menu;
    }
}
