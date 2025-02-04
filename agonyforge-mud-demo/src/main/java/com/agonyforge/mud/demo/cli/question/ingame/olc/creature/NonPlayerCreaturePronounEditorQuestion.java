package com.agonyforge.mud.demo.cli.question.ingame.olc.creature;

import com.agonyforge.mud.core.cli.Color;
import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.cli.menu.impl.MenuItem;
import com.agonyforge.mud.core.cli.menu.impl.MenuPane;
import com.agonyforge.mud.core.cli.menu.impl.MenuPrompt;
import com.agonyforge.mud.core.cli.menu.impl.MenuTitle;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.cli.question.BaseQuestion;
import com.agonyforge.mud.demo.model.constant.Pronoun;
import com.agonyforge.mud.demo.model.impl.MudCharacterTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Locale;

@Component
public class NonPlayerCreaturePronounEditorQuestion extends BaseQuestion {
    public static final String MEDIT_MODEL = "MEDIT.MODEL";

    private final MenuPane menuPane = new MenuPane();

    @Autowired
    public NonPlayerCreaturePronounEditorQuestion(ApplicationContext applicationContext,
                                                  RepositoryBundle repositoryBundle) {
        super(applicationContext, repositoryBundle);

        menuPane.setTitle(new MenuTitle("Non-Player Character Pronouns"));
        menuPane.setPrompt(new MenuPrompt());
    }

    @Override
    public Output prompt(WebSocketContext wsContext) {
        MudCharacterTemplate npcTemplate = getRepositoryBundle().getCharacterPrototypeRepository().findById((Long)wsContext.getAttributes().get(MEDIT_MODEL)).orElseThrow();

        populateMenuItems(npcTemplate);
        return menuPane.render(Color.DYELLOW, Color.DWHITE);
    }

    @Override
    public Response answer(WebSocketContext webSocketContext, Input input) {
        String nextQuestion = "nonPlayerCreaturePronounEditorQuestion";
        Output output = new Output();
        MudCharacterTemplate npcTemplate = getRepositoryBundle().getCharacterPrototypeRepository().findById((Long)webSocketContext.getAttributes().get(MEDIT_MODEL)).orElseThrow();

        String choice = input.getInput().toUpperCase(Locale.ROOT);

        try {
            int ordinal = Integer.parseInt(choice);

            if (ordinal < 1 || ordinal > Pronoun.values().length) {
                output.append("[red]Please choose one of the menu items.");
            } else {
                Pronoun pronoun = Pronoun.values()[ordinal - 1];
                npcTemplate.getCharacter().setPronoun(pronoun);
                getRepositoryBundle().getCharacterPrototypeRepository().save(npcTemplate);
                nextQuestion = "nonPlayerCreatureEditorQuestion";
            }
        } catch (NumberFormatException e) {
            if ("X".equals(choice)) {
                nextQuestion = "nonPlayerCreatureEditorQuestion";
            } else {
                output.append("[red]Please choose one of the menu items.");
            }
        }

        Question next = getQuestion(nextQuestion);

        return new Response(next, output);
    }

    private void populateMenuItems(MudCharacterTemplate npcTemplate) {
        menuPane.getItems().clear();

        menuPane.getTitle().setTitle(String.format("Non-Player Character Pronouns - %d", npcTemplate.getId()));

        Arrays.stream(Pronoun.values())
            .forEach(pronoun -> menuPane.getItems().add(new MenuItem(
                Integer.toString(pronoun.ordinal() + 1),
                pronoun.toString(),
                pronoun)));

        menuPane.getItems().add(new MenuItem("X", "Back"));
    }
}
