package com.agonyforge.mud.demo.cli.menu;

import com.agonyforge.mud.core.cli.Color;
import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.BaseQuestion;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

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
        String choice = input.getInput();

        switch(choice) {
            case "1" -> ch.setStrength(ch.getStrength() + 1);
            case "2" -> ch.setDexterity(ch.getDexterity() + 1);
            case "3" -> ch.setConstitution(ch.getConstitution() + 1);
            case "4" -> ch.setIntelligence(ch.getIntelligence() + 1);
            case "5" -> ch.setWisdom(ch.getWisdom() + 1);
            case "6" -> ch.setCharisma(ch.getCharisma() + 1);
        }

        getRepositoryBundle().getCharacterRepository().save(ch);

        if (computeAttributePoints(ch) >= 6) {
            output.append("[green]Character attributes saved!");
            nextQuestion = "characterMenuQuestion";
        }

        Question next = getQuestion(nextQuestion);

        return new Response(next, output);
    }

    private int computeAttributePoints(MudCharacter ch) {
        return ch.getStrength()
            + ch.getDexterity()
            + ch.getConstitution()
            + ch.getIntelligence()
            + ch.getWisdom()
            + ch.getCharisma();
    }

    private void populateMenuItems(MudCharacter ch) {
        menuPane.getItems().clear();

        int points = STARTING_ATTRIBUTES - computeAttributePoints(ch);

        menuPane.getItems().add(new MenuItem(" ", String.format("[default]Allocate [white]%d more points [default]for your attributes.", points)));
        menuPane.getItems().add(new MenuItem("1", String.format("Strength     (%d)", ch.getStrength())));
        menuPane.getItems().add(new MenuItem("2", String.format("Dexterity    (%d)", ch.getDexterity())));
        menuPane.getItems().add(new MenuItem("3", String.format("Constitution (%d)", ch.getConstitution())));
        menuPane.getItems().add(new MenuItem("4", String.format("Intelligence (%d)", ch.getIntelligence())));
        menuPane.getItems().add(new MenuItem("5", String.format("Wisdom       (%d)", ch.getWisdom())));
        menuPane.getItems().add(new MenuItem("6", String.format("Charisma     (%d)", ch.getCharisma())));
    }
}
