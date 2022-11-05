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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class MenuQuestion implements Question {
    private final DemoMenuPane menuPane = new DemoMenuPane();
    private final Question nextQuestion;

    @Autowired
    public MenuQuestion(@Qualifier("echoQuestion") Question nextQuestion) {
        this.nextQuestion = nextQuestion;

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
        Output output = new Output();
        Question next = nextQuestion;

        switch (input.getInput().toUpperCase()) {
            case "F": output.append("Bar!"); break;
            case "B": output.append("Baz!"); break;
            case "C": output.append("I'm the only sane one around here."); break;
            case "Z": output.append("Royale with cheese."); break;
            case "P": output.append("Rico Suave!"); break;
            default:
                output.append("Please choose one of the menu options.");
                next = this;
        }
        return new Response(next, output);
    }
}
