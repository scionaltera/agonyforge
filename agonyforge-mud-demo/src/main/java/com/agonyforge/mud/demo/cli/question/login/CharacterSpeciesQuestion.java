package com.agonyforge.mud.demo.cli.question.login;

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
import com.agonyforge.mud.demo.model.constant.Effort;
import com.agonyforge.mud.demo.model.constant.Stat;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudSpecies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.agonyforge.mud.demo.model.impl.ModelConstants.TYPE_SPECIES;

@Component
public class CharacterSpeciesQuestion extends BaseQuestion {
    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterSpeciesQuestion.class);

    private final MenuPane menuPane = new MenuPane();

    @Autowired
    public CharacterSpeciesQuestion(ApplicationContext applicationContext, RepositoryBundle repositoryBundle) {
        super(applicationContext, repositoryBundle);

        menuPane.setTitle(new MenuTitle("Choose Your Species"));
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

        String nextQuestion = "characterSpeciesQuestion";
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
                MudSpecies species = (MudSpecies)item.getItem();

                ch.setSpeciesId(species.getId());

                Arrays.stream(Stat.values()).forEach(stat -> ch.setSpeciesStat(stat, species.getStat(stat)));
                Arrays.stream(Effort.values()).forEach(effort -> ch.setSpeciesEffort(effort, species.getEffort(effort)));

                getRepositoryBundle().getCharacterRepository().save(ch);
            }

            nextQuestion = "characterProfessionQuestion";
        }

        Question next = getQuestion(nextQuestion);

        return new Response(next, output);
    }

    private void populateMenuItems() {
        menuPane.getItems().clear();

        getRepositoryBundle().getSpeciesRepository().findAll()
            .stream()
            .sorted(Comparator.comparing(MudSpecies::getName, String::compareToIgnoreCase))
            .forEach(species -> {
                List<String> buffs = new ArrayList<>();

                Arrays.stream(Stat.values())
                        .filter(stat -> species.getStat(stat) != 0)
                        .forEach(stat -> buffs.add(String.format("%s %s%d",
                            stat.getAbbreviation(),
                            species.getStat(stat) >= 0 ? "+" : "-",
                            species.getStat(stat))));

                Arrays.stream(Effort.values())
                    .filter(effort -> species.getEffort(effort) != 0)
                    .forEach(effort -> buffs.add(String.format("%s %s%d",
                        effort.getName(),
                        species.getEffort(effort) >= 0 ? "+" : "-",
                        species.getEffort(effort))));

                menuPane.getItems().add(new MenuItem(
                    Integer.toString(menuPane.getItems().size() + 1),
                    // the formatting below is fragile and will need to be adjusted when you add
                    // any species with a name longer than "Human"
                    String.format("%6s: %s", species.getName(), String.join(", ", buffs)),
                    species
                ));
            });
    }
}
