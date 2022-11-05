package com.agonyforge.mud.cli.menu;

import com.agonyforge.mud.web.model.Output;

public interface MenuComponent {
    Output render(Color ... colors);
}
