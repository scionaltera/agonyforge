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
import com.agonyforge.mud.demo.model.constant.Pronoun;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
public class CharacterPronounQuestion extends BaseQuestion {
    private final MenuPane menuPane = new MenuPane();

    @Autowired
    public CharacterPronounQuestion(ApplicationContext applicationContext,
                                 RepositoryBundle repositoryBundle) {
        super(applicationContext, repositoryBundle);

        menuPane.setTitle(new MenuTitle("Choose Your Pronouns"));
        menuPane.setPrompt(new MenuPrompt());
    }

    @Override
    public Output prompt(WebSocketContext wsContext) {
        populateMenuItems();

        return menuPane.render(Color.WHITE, Color.BLACK);
    }

    @Override
    public Response answer(WebSocketContext webSocketContext, Input input) {
        populateMenuItems();

        String nextQuestion = "characterPronounQuestion";
        Output output = new Output();
        String choice = input.getInput().toUpperCase();
        Optional<MenuItem> itemOptional = menuPane.getItems()
            .stream()
            .map(i -> (MenuItem)i)
            .filter(i -> choice.equals(i.getKey()))
            .findFirst();

        if (itemOptional.isEmpty()) {
            output.append("[red]Please choose one of the menu options.");
        } else {
            MenuItem item = itemOptional.get();
            Optional<MudCharacter> chOptional = getCharacter(webSocketContext, output);

            if (chOptional.isPresent()) {
                MudCharacter ch = chOptional.get();
                ch.getCharacter().setPronoun((Pronoun)item.getItem());
                getRepositoryBundle().getCharacterRepository().save(ch);
            }

            nextQuestion = "characterStatQuestion";
        }

        Question next = getQuestion(nextQuestion);

        return new Response(next, output);
    }

    private void populateMenuItems() {
        menuPane.getItems().clear();

        Arrays
            .stream(Pronoun.values())
            .sorted()
            .forEach(pronoun -> menuPane.getItems().add(new MenuItem(
                Integer.toString(menuPane.getItems().size() + 1),
                String.format("%s/%s", pronoun.getSubject(), pronoun.getObject()),
                pronoun)));
    }
}
