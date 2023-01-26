package com.agonyforge.mud.demo.cli.menu;

import com.agonyforge.mud.core.cli.Color;
import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.DemoQuestion;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.impl.Pronoun;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;

@Component
public class CharacterPronounQuestion extends DemoQuestion {
    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterPronounQuestion.class);

    private final DemoMenuPane menuPane = new DemoMenuPane();

    @Autowired
    public CharacterPronounQuestion(ApplicationContext applicationContext,
                                 MudCharacterRepository characterRepository,
                                 MudItemRepository itemRepository) {
        super(applicationContext, characterRepository, itemRepository);

        menuPane.setTitle(new DemoMenuTitle("Choose Your Pronouns"));
        menuPane.setPrompt(new DemoMenuPrompt());
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
        Optional<DemoMenuItem> itemOptional = menuPane.getItems()
            .stream()
            .map(i -> (DemoMenuItem)i)
            .filter(i -> choice.equals(i.getKey()))
            .findFirst();

        if (itemOptional.isEmpty()) {
            output.append("[red]Please choose one of the menu options.");
        } else {
            DemoMenuItem item = itemOptional.get();
            Optional<MudCharacter> chOptional = getCharacter(webSocketContext, output);

            if (chOptional.isPresent()) {
                MudCharacter ch = chOptional.get();
                ch.setPronoun((Pronoun)item.getItem());
                getCharacterRepository().save(ch);
            }

            nextQuestion = "characterMenuQuestion";
        }

        Question next = getQuestion(nextQuestion);

        return new Response(next, output);
    }

    private void populateMenuItems() {
        menuPane.getItems().clear();

        Arrays
            .stream(Pronoun.values())
            .sorted()
            .forEach(pronoun -> menuPane.getItems().add(new DemoMenuItem(
                Integer.toString(menuPane.getItems().size()),
                String.format("%s/%s", pronoun.getSubject(), pronoun.getObject()),
                pronoun)));
    }
}
