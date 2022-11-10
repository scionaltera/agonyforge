package com.agonyforge.mud.core.cli.menu;

import com.agonyforge.mud.core.cli.Color;
import com.agonyforge.mud.core.web.model.Output;

public abstract class AbstractMenuDivider implements MenuComponent {
    private int length;

    public AbstractMenuDivider(int length) {
        setLength(length);
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public abstract Output render(Color... colors);
}
