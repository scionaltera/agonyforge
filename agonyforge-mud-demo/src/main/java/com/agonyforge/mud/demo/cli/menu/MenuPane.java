package com.agonyforge.mud.demo.cli.menu;

import com.agonyforge.mud.core.cli.menu.AbstractMenuPane;
import com.agonyforge.mud.core.cli.Color;
import com.agonyforge.mud.core.web.model.Output;

public class MenuPane extends AbstractMenuPane {
    public Output render(Output menu, Color... colors) {
        if (getTitle() != null) {
            menu.append(getTitle().render(colors));
        }

        getItems().forEach(item -> menu.append(item.render(colors)));

        if (getPrompt() != null) {
            menu.append(getPrompt().render(colors));
        }

        return menu;
    }

    @Override
    public Output render(Color... colors) {
        Output menu = new Output();

        return render(menu, colors);
    }
}
