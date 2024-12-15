package com.agonyforge.mud.demo.cli.question.login;

import com.agonyforge.mud.core.cli.Color;
import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.core.cli.menu.impl.MenuItem;
import com.agonyforge.mud.core.cli.menu.impl.MenuPane;
import com.agonyforge.mud.core.cli.menu.impl.MenuPrompt;
import com.agonyforge.mud.core.cli.menu.impl.MenuTitle;
import com.agonyforge.mud.demo.cli.question.BaseQuestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Optional;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_PCHARACTER;

@Component
public class CharacterMenuQuestion extends BaseQuestion {
    private final MenuPane menuPane = new MenuPane();

    @Autowired
    public CharacterMenuQuestion(ApplicationContext applicationContext,
                                 RepositoryBundle repositoryBundle) {
        super(applicationContext, repositoryBundle);

        menuPane.setTitle(new MenuTitle("Your Characters"));
        menuPane.setPrompt(new MenuPrompt());
    }

    @Override
    public Output prompt(WebSocketContext wsContext) {
        populateMenuItems(wsContext.getPrincipal());

        return menuPane.render(Color.WHITE, Color.BLACK);
    }

    @Override
    public Response answer(WebSocketContext wsContext, Input input) {
        populateMenuItems(wsContext.getPrincipal());

        String nextQuestion = "characterMenuQuestion";
        Output output = new Output();
        String choice = input.getInput().toUpperCase();
        Optional<MenuItem> itemOptional = menuPane.getItems()
            .stream()
            .map(i -> (MenuItem)i)
            .filter(i -> choice.equals(i.getKey()))
            .findFirst();

        if (itemOptional.isEmpty()) {
            output.append("[red]Please choose one of the menu options.");
        } else if ("N".equals(choice)) {
            nextQuestion = "characterNameQuestion";
        } else {
            MenuItem item = itemOptional.get();
            wsContext.getAttributes().put(MUD_PCHARACTER, item.getItem());
            nextQuestion = "characterViewQuestion";
        }

        Question next = getQuestion(nextQuestion);

        return new Response(next, output);
    }

    private void populateMenuItems(Principal principal) {
        menuPane.getItems().clear();
        menuPane.getItems().add(new MenuItem("N", "New Character"));

        getRepositoryBundle().getCharacterPrototypeRepository().findByUsername(principal.getName())
            .forEach(ch -> menuPane.getItems().add(new MenuItem(
                Integer.toString(menuPane.getItems().size()),
                String.format("%s%s", ch.getName(), ch.getComplete() ? "" : " [dred]*[red]INCOMPLETE[dred]*"),
                ch.getId())));
    }
}
