package com.agonyforge.mud.core.cli.menu;

import com.agonyforge.mud.core.cli.Color;
import com.agonyforge.mud.core.web.model.Output;

public interface MenuComponent {
    Output render(Color... colors);
}
