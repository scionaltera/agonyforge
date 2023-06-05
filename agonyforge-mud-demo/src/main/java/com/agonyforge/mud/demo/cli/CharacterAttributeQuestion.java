package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.core.cli.Color;
import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.menu.MenuItem;
import com.agonyforge.mud.demo.cli.menu.MenuPane;
import com.agonyforge.mud.demo.cli.menu.MenuPrompt;
import com.agonyforge.mud.demo.cli.menu.MenuTitle;
import com.agonyforge.mud.models.dynamodb.constant.Stat;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Locale;

@Component
public class CharacterAttributeQuestion extends BaseQuestion {
    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterAttributeQuestion.class);
    private static final int STARTING_ATTRIBUTES = 6;

    private final MenuPane menuPane = new MenuPane();

    public CharacterAttributeQuestion(ApplicationContext applicationContext,
                                      RepositoryBundle repositoryBundle) {
        super(applicationContext, repositoryBundle);

        menuPane.setTitle(new MenuTitle("Allocate Attribute Points"));
        menuPane.setPrompt(new MenuPrompt());
    }

    @Override
    public Output prompt(WebSocketContext wsContext) {
        Output output = new Output();
        MudCharacter ch = getCharacter(wsContext, output).orElseThrow();

        populateMenuItems(ch);

        return menuPane.render(Color.WHITE, Color.BLACK);
    }

    @Override
    public Response answer(WebSocketContext webSocketContext, Input input) {
        String nextQuestion = "characterAttributeQuestion";
        Output output = new Output();
        MudCharacter ch = getCharacter(webSocketContext, output).orElseThrow();
        String choice = input.getInput().toUpperCase(Locale.ENGLISH);
        int totalPoints = computeAttributePoints(ch);

        if (choice.contains("+")) {
            if (totalPoints >= STARTING_ATTRIBUTES) {
                output.append("[red]You don't have any more points to allocate!");
            } else {
                try {
                    int i = Integer.parseInt(choice.substring(0, choice.length() - 1)) - 1;
                    ch.addStat(Stat.values()[i], 1);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    output.append("[red][red]Oops! Try a number with a plus or minus!");
                }
            }
        } else if (choice.contains("-")) {
            if (totalPoints <= 0) {
                output.append("[red]You haven't assigned any of your points yet!");
            } else {
                try {
                    int i = Integer.parseInt(choice.substring(0, choice.length() - 1)) - 1;
                    ch.addStat(Stat.values()[i], -1);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    output.append("[red][red]Oops! Try a number with a plus or minus!");
                }
            }
        } else {
            if (choice.equals("S")) {
                if (totalPoints == STARTING_ATTRIBUTES) {
                    output.append("[green]Character attributes saved!");
                    nextQuestion = "characterMenuQuestion";
                } else {
                    output.append("[red]Please allocate exactly 6 points for your attributes.");
                }
            } else {
                output.append("[red]Oops! Try a number with a plus or minus!");
            }
        }

        getRepositoryBundle().getCharacterRepository().save(ch);

        Question next = getQuestion(nextQuestion);

        return new Response(next, output);
    }

    private int computeAttributePoints(MudCharacter ch) {
        return Arrays.stream(Stat.values())
            .map(ch::getStat)
            .reduce(0, Integer::sum);
    }

    private void populateMenuItems(MudCharacter ch) {
        menuPane.getItems().clear();

        int points = STARTING_ATTRIBUTES - computeAttributePoints(ch);

        menuPane.getItems().add(new MenuItem(" ", "[default]Enter the menu number and a plus (+) or minus (-) to add or subtract from an attribute!"));
        menuPane.getItems().add(new MenuItem(" ", "[default]For example, '3+' to raise CON or '6-' to lower CHA."));
        menuPane.getItems().add(new MenuItem(" ", String.format("[default]Allocate [white]%d more points [default]for your attributes.", points)));

        Arrays.stream(Stat.values())
                .forEachOrdered(stat -> menuPane.getItems().add(new MenuItem((stat.ordinal() + 1) + "[+/-]", String.format("%15s (%d)", stat.getName(), ch.getStat(stat)))));

        menuPane.getItems().add(new MenuItem("S", "Save"));
    }
}
