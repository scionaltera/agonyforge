package com.agonyforge.mud.demo.cli.menu;

import com.agonyforge.mud.core.cli.menu.AbstractMenuPane;
import com.agonyforge.mud.core.cli.Color;
import com.agonyforge.mud.core.web.model.Output;

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
