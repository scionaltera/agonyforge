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
import com.agonyforge.mud.demo.model.impl.MudProfession;
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

import static com.agonyforge.mud.demo.model.impl.ModelConstants.TYPE_PROFESSION;

@Component
public class CharacterProfessionQuestion extends BaseQuestion {
    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterProfessionQuestion.class);

    private final MenuPane menuPane = new MenuPane();

    @Autowired
    public CharacterProfessionQuestion(ApplicationContext applicationContext, RepositoryBundle repositoryBundle) {
        super(applicationContext, repositoryBundle);

        menuPane.setTitle(new MenuTitle("Choose Your Profession"));
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

        String nextQuestion = "characterProfessionQuestion";
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
                MudProfession profession = (MudProfession)item.getItem();

                ch.setProfessionId(profession.getId());

                Arrays.stream(Stat.values()).forEach(stat -> ch.setProfessionStat(stat, profession.getStat(stat)));
                Arrays.stream(Effort.values()).forEach(effort -> ch.setProfessionEffort(effort, profession.getEffort(effort)));

                getRepositoryBundle().getCharacterRepository().save(ch);
            }

            nextQuestion = "characterMenuQuestion";
        }

        Question next = getQuestion(nextQuestion);

        return new Response(next, output);
    }

    private void populateMenuItems() {
        menuPane.getItems().clear();

        getRepositoryBundle().getProfessionRepository().getByType(TYPE_PROFESSION)
            .stream()
            .sorted(Comparator.comparing(MudProfession::getName, String::compareToIgnoreCase))
            .forEach(profession -> {
                List<String> buffs = new ArrayList<>();

                Arrays.stream(Stat.values())
                    .filter(stat -> profession.getStat(stat) != 0)
                    .forEach(stat -> buffs.add(String.format("%s %s%d",
                        stat.getAbbreviation(),
                        profession.getStat(stat) >= 0 ? "+" : "-",
                        profession.getStat(stat))));

                Arrays.stream(Effort.values())
                    .filter(effort -> profession.getEffort(effort) != 0)
                    .forEach(effort -> buffs.add(String.format("%s %s%d",
                        effort.getName(),
                        profession.getEffort(effort) >= 0 ? "+" : "-",
                        profession.getEffort(effort))));

                menuPane.getItems().add(new MenuItem(
                   Integer.toString(menuPane.getItems().size() + 1),
                   // the formatting below is fragile and will need to be adjusted when you add
                   // any profession with a name longer than "Human"
                   String.format("%7s: %s", profession.getName(), String.join(", ", buffs)),
                   profession
                ));
            });
    }
}
