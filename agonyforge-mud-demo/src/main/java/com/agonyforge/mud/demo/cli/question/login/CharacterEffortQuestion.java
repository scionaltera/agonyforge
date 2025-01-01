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
import com.agonyforge.mud.demo.model.constant.Effort;
import com.agonyforge.mud.demo.model.impl.MudCharacterPrototype;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Locale;

@Component
public class CharacterEffortQuestion extends BaseQuestion {
    public static final int STARTING_EFFORTS = 4;

    private final MenuPane menuPane = new MenuPane();

    public CharacterEffortQuestion(ApplicationContext applicationContext,
                                   RepositoryBundle repositoryBundle) {
        super(applicationContext, repositoryBundle);

        menuPane.setTitle(new MenuTitle("Allocate Effort Bonuses"));
        menuPane.setPrompt(new MenuPrompt());
    }

    @Override
    public Output prompt(WebSocketContext wsContext) {
        Output output = new Output();
        MudCharacterPrototype ch = getCharacterPrototype(wsContext, output).orElseThrow();

        populateMenuItems(ch);

        return menuPane.render(Color.WHITE, Color.BLACK);
    }

    @Override
    public Response answer(WebSocketContext webSocketContext, Input input) {
        String nextQuestion = "characterEffortQuestion";
        Output output = new Output();
        MudCharacterPrototype ch = getCharacterPrototype(webSocketContext, output).orElseThrow();
        String choice = input.getInput().toUpperCase(Locale.ROOT);
        int totalPoints = computeEffortPoints(ch);

        if (choice.contains("+")) {
            if (totalPoints >= STARTING_EFFORTS) {
                output.append("[red]You don't have any more points to allocate!");
            } else {
                try {
                    int i = Integer.parseInt(choice.substring(0, choice.length() - 1)) - 1;
                    ch.getCharacter().addBaseEffort(Effort.values()[i], 1);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    output.append("[red]Oops! Try a number with a plus or minus!");
                }
            }
        } else if (choice.contains("-")) {
            if (totalPoints <= 0) {
                output.append("[red]You haven't assigned any of your points yet!");
            } else {
                try {
                    int i = Integer.parseInt(choice.substring(0, choice.length() - 1)) - 1;
                    ch.getCharacter().addBaseEffort(Effort.values()[i], -1);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    output.append("[red]Oops! Try a number with a plus or minus!");
                }
            }
        } else {
            if (choice.equals("S")) {
                if (totalPoints == STARTING_EFFORTS) {
                    output.append("[green]Character efforts saved!");
                    nextQuestion = "characterSpeciesQuestion";
                } else {
                    output.append("[red]Please allocate exactly %d points for your efforts.", STARTING_EFFORTS);
                }
            } else {
                output.append("[red]Oops! Try a number with a plus or minus!");
            }
        }

        getRepositoryBundle().getCharacterPrototypeRepository().save(ch);

        Question next = getQuestion(nextQuestion);

        return new Response(next, output);
    }

    private int computeEffortPoints(MudCharacterPrototype ch) {
        return Arrays.stream(Effort.values())
            .map(ch.getCharacter()::getBaseEffort)
            .reduce(0, Integer::sum);
    }

    private void populateMenuItems(MudCharacterPrototype ch) {
        menuPane.getItems().clear();

        int points = STARTING_EFFORTS - computeEffortPoints(ch);

        menuPane.getItems().add(new MenuItem(" ", "[default]Enter the menu number and a plus (+) or minus (-) to add or subtract from an effort. For example, '1+' to raise 'Basic' or '5-' to lower 'Ultimate'."));
        menuPane.getItems().add(new MenuItem(" ", String.format("[default]Please allocate [white]%d more points [default]for your stats.", points)));

        Arrays.stream(Effort.values())
            .forEachOrdered(effort -> menuPane.getItems().add(new MenuItem((effort.ordinal() + 1) + "[+/-]", String.format("%16s (%d)", effort.getName(), ch.getCharacter().getBaseEffort(effort)))));

        menuPane.getItems().add(new MenuItem("S", "Save"));
    }
}
