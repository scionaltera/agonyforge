package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.core.cli.Color;
import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.demo.cli.menu.DemoMenuItem;
import com.agonyforge.mud.demo.cli.menu.DemoMenuPane;
import com.agonyforge.mud.demo.cli.menu.DemoMenuPrompt;
import com.agonyforge.mud.demo.cli.menu.DemoMenuTitle;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.session.Session;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Optional;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;

@Component
public class CharacterMenuQuestion extends DemoQuestion {
    private final DemoMenuPane menuPane = new DemoMenuPane();

    @Autowired
    public CharacterMenuQuestion(ApplicationContext applicationContext,
                                 MudCharacterRepository characterRepository) {
        super(applicationContext, characterRepository);

        menuPane.setTitle(new DemoMenuTitle("Your Characters"));
        menuPane.setPrompt(new DemoMenuPrompt());
    }

    @Override
    public Output prompt(Principal principal, Session session) {
        populateMenuItems(principal);

        return menuPane.render(Color.WHITE, Color.BLACK);
    }

    @Override
    public Response answer(Principal principal, Session session, Input input) {
        populateMenuItems(principal);

        String nextQuestion = "characterMenuQuestion";
        Output output = new Output();
        String choice = input.getInput().toUpperCase();
        Optional<DemoMenuItem> itemOptional = menuPane.getItems()
            .stream()
            .map(i -> (DemoMenuItem)i)
            .filter(i -> choice.equals(i.getKey()))
            .findFirst();

        if (itemOptional.isEmpty()) {
            output.append("[red]Please choose one of the menu options.");
        } else if ("N".equals(choice)) {
            nextQuestion = "characterNameQuestion";
        } else {
            DemoMenuItem item = itemOptional.get();
            session.setAttribute(MUD_CHARACTER, item.getItem());
            nextQuestion = "characterViewQuestion";
        }

        Question next = getQuestion(nextQuestion);

        return new Response(next, output);
    }

    private void populateMenuItems(Principal principal) {
        menuPane.getItems().clear();
        menuPane.getItems().add(new DemoMenuItem("N", "New Character"));

        getCharacterRepository().getByUser(principal.getName())
            .forEach(ch -> menuPane.getItems().add(new DemoMenuItem(
                Integer.toString(menuPane.getItems().size()),
                ch.getName(),
                ch.getId())));
    }
}
