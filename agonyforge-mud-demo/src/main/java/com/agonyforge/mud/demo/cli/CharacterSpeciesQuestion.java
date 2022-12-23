package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.core.cli.Color;
import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.menu.DemoMenuItem;
import com.agonyforge.mud.demo.cli.menu.DemoMenuPane;
import com.agonyforge.mud.demo.cli.menu.DemoMenuPrompt;
import com.agonyforge.mud.demo.cli.menu.DemoMenuTitle;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.impl.Species;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudItemRepository;
import com.agonyforge.mud.models.dynamodb.repository.SpeciesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CharacterSpeciesQuestion extends DemoQuestion {
    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterSpeciesQuestion.class);

    private final SpeciesRepository speciesRepository;
    private final DemoMenuPane menuPane = new DemoMenuPane();

    @Autowired
    public CharacterSpeciesQuestion(ApplicationContext applicationContext,
                                    MudCharacterRepository characterRepository,
                                    MudItemRepository itemRepository,
                                    SpeciesRepository speciesRepository) {
        super(applicationContext, characterRepository, itemRepository);

        this.speciesRepository = speciesRepository;

        menuPane.setTitle(new DemoMenuTitle("Choose Species"));
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
            return new Response(this, output);
        } else {
            DemoMenuItem item = itemOptional.get();
            Optional<MudCharacter> chOptional = getCharacter(webSocketContext, output);

            if (chOptional.isEmpty()) {
                LOGGER.error("Character not found in webSocketContext");

                Question next = getQuestion(nextQuestion);
                return new Response(next, output);
            }

            MudCharacter ch = chOptional.get();

            ch.setSpecies((UUID)item.getItem());
            getCharacterRepository().save(ch);

            output.append(String.format("[default]Hello, [white]%s[default]!", ch.getName()));
        }

        Question next = getQuestion(nextQuestion);
        return new Response(next, output);
    }

    private void populateMenuItems() {
        menuPane.getItems().clear();

        speciesRepository.getAll()
            .stream()
            .filter(Species::isPlayable)
            .forEach(species -> menuPane.getItems().add(new DemoMenuItem(
                Integer.toString(menuPane.getItems().size()),
                species.getName(),
                species.getId())));
    }
}
