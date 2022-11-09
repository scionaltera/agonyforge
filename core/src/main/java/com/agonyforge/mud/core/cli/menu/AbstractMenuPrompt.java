package com.agonyforge.mud.core.cli.menu;

import com.agonyforge.mud.core.cli.Color;
import com.agonyforge.mud.core.web.model.Output;

public abstract class AbstractMenuPrompt implements MenuComponent {
    @Override
    public abstract Output render(Color... colors);
}
