package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.cli.menu.Color;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.demo.cli.menu.DemoMenuItem;
import com.agonyforge.mud.demo.cli.menu.DemoMenuPane;
import com.agonyforge.mud.demo.cli.menu.DemoMenuPrompt;
import com.agonyforge.mud.demo.cli.menu.DemoMenuTitle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Map;

@Component
public class MenuQuestion implements Question {
    private final DemoMenuPane menuPane = new DemoMenuPane();
    private final Question nextQuestion;
    private final FindByIndexNameSessionRepository<Session> sessionRepository;

    @Autowired
    public MenuQuestion(@Qualifier("echoQuestion") Question nextQuestion, FindByIndexNameSessionRepository<Session> sessionRepository) {
        this.nextQuestion = nextQuestion;
        this.sessionRepository = sessionRepository;

        assert this.sessionRepository != null;

        menuPane.setTitle(new DemoMenuTitle("Demo Menu"));
        menuPane.getItems().add(new DemoMenuItem("S", "Session ID"));
        menuPane.getItems().add(new DemoMenuItem("F", "Foo"));
        menuPane.getItems().add(new DemoMenuItem("B", "Bar"));
        menuPane.getItems().add(new DemoMenuItem("C", "Crazy Town"));
        menuPane.getItems().add(new DemoMenuItem("Z", "Zed's Dead, Baby"));
        menuPane.getItems().add(new DemoMenuItem("P", "Puerto Rico"));
        menuPane.setPrompt(new DemoMenuPrompt());
    }

    @Override
    public Output prompt(Principal principal) {
        return menuPane.render(Color.CYAN, Color.DCYAN);
    }

    @Override
    public Response answer(Principal principal, Input input) {
        Output output = new Output();
        Question next = nextQuestion;

        switch (input.getInput().toUpperCase()) {
            case "S":
                output.append(listSessions(sessionRepository.findByPrincipalName(principal.getName())));
                next = this;
                break;
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

    private Output listSessions(Map<String, Session> sessions) {
        Output output = new Output();

        output.append("Your sessions:");
        sessions.keySet().forEach(key -> {
            Session session = sessions.get(key);
            SecurityContext ctx = session.getAttribute("SPRING_SECURITY_CONTEXT");
            WebAuthenticationDetails details = (WebAuthenticationDetails) ctx.getAuthentication().getDetails();

            int count = session.getAttributeOrDefault("MENU.DEMO", 1);
            session.setAttribute("MENU.DEMO", count + 1);

            output.append(key + " @ " + details.getRemoteAddress() + " (" + count + " times)");
        });

        return output;
    }
}
