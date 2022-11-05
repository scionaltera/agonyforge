package com.agonyforge.mud.cli.menu;

import com.agonyforge.mud.web.model.Output;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMenuPane implements MenuComponent {
    private String title;
    private final List<AbstractMenuItem> items = new ArrayList<>();
    private AbstractMenuPrompt prompt;

    public AbstractMenuPane(String title) {
        setTitle(title);
    }

    public List<AbstractMenuItem> getItems() {
        return items;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public AbstractMenuPrompt getPrompt() {
        return prompt;
    }

    public void setPrompt(AbstractMenuPrompt prompt) {
        this.prompt = prompt;
    }

    @Override
    public abstract Output render(Color... colors);
}
