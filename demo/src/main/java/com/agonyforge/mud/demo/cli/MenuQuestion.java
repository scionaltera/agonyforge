package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.core.cli.AbstractQuestion;
import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.cli.Color;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.demo.cli.menu.DemoMenuItem;
import com.agonyforge.mud.demo.cli.menu.DemoMenuPane;
import com.agonyforge.mud.demo.cli.menu.DemoMenuPrompt;
import com.agonyforge.mud.demo.cli.menu.DemoMenuTitle;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Map;

@Component
public class MenuQuestion extends AbstractQuestion {
    private final DemoMenuPane menuPane = new DemoMenuPane();
    private final ApplicationContext applicationContext;

    private String nextQuestion = "menuQuestion";

    public MenuQuestion(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;

        menuPane.setTitle(new DemoMenuTitle("Demo Menu"));
        menuPane.getItems().add(new DemoMenuItem("F", "Foo"));
        menuPane.getItems().add(new DemoMenuItem("B", "Bar"));
        menuPane.getItems().add(new DemoMenuItem("C", "Crazy Town"));
        menuPane.getItems().add(new DemoMenuItem("Z", "Zed's Dead, Baby"));
        menuPane.getItems().add(new DemoMenuItem("P", "Puerto Rico"));
        menuPane.setPrompt(new DemoMenuPrompt());
    }

    public void setNextQuestion(String nextQuestion) {
        this.nextQuestion = nextQuestion;
    }

    @Override
    public Output prompt(Principal principal, Map<String, Object> stompSession) {
        return menuPane.render(Color.CYAN, Color.DCYAN);
    }

    @Override
    public Response answer(Principal principal, Map<String, Object> stompSession, Input input) {
        Output output = new Output();
        Question next = applicationContext.getBean(nextQuestion, Question.class);

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
