package com.agonyforge.mud.cli.demo;

import com.agonyforge.mud.cli.Question;
import com.agonyforge.mud.cli.Response;
import com.agonyforge.mud.cli.menu.Color;
import com.agonyforge.mud.cli.menu.demo.DemoMenuItem;
import com.agonyforge.mud.cli.menu.demo.DemoMenuPane;
import com.agonyforge.mud.cli.menu.demo.DemoMenuPrompt;
import com.agonyforge.mud.cli.menu.demo.DemoMenuTitle;
import com.agonyforge.mud.web.model.Input;
import com.agonyforge.mud.web.model.Output;

public class MenuQuestion implements Question {
    private final DemoMenuPane menuPane = new DemoMenuPane();

    public MenuQuestion() {
        menuPane.setTitle(new DemoMenuTitle("Demo Menu"));
        menuPane.getItems().add(new DemoMenuItem("F", "Foo"));
        menuPane.getItems().add(new DemoMenuItem("B", "Bar"));
        menuPane.getItems().add(new DemoMenuItem("C", "Crazy Town"));
        menuPane.getItems().add(new DemoMenuItem("Z", "Zed's Dead, Baby"));
        menuPane.getItems().add(new DemoMenuItem("P", "Puerto Rico"));
        menuPane.setPrompt(new DemoMenuPrompt());
    }

    @Override
    public Output prompt() {
        return menuPane.render(Color.CYAN, Color.DCYAN);
    }

    @Override
    public Response answer(Input input) {
        return new Response(new EchoQuestion(), new Output(input.getInput() + " it is, then."));
    }
}
