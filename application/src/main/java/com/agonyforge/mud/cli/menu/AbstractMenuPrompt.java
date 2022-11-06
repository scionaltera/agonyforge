package com.agonyforge.mud.cli.menu;

import com.agonyforge.mud.web.model.Output;

public abstract class AbstractMenuPrompt implements MenuComponent {
    @Override
    public abstract Output render(Color... colors);
}
