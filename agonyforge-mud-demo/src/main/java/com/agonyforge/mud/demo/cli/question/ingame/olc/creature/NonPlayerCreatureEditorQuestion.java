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
import com.agonyforge.mud.demo.cli.question.login.CharacterSheetFormatter;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudCharacterTemplate;
import com.agonyforge.mud.demo.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class NonPlayerCreatureEditorQuestion extends BaseQuestion {
    public static final String MEDIT_MODEL = "MEDIT.MODEL";

    static final String MEDIT_STATE = "MEDIT.STATE";

    private enum MeditState {
        NAME,
        PRONOUN,
        HEARTS
    }

    private final CommService commService;
    private final MenuPane menuPane = new MenuPane();

    @Autowired
    public NonPlayerCreatureEditorQuestion(ApplicationContext applicationContext,
                                           RepositoryBundle repositoryBundle,
                                           CommService commService) {
        super(applicationContext, repositoryBundle);

        this.commService = commService;

        menuPane.setTitle(new MenuTitle("Non-Player Character Editor"));
        menuPane.setPrompt(new MenuPrompt());
    }

    @Override
    public Output prompt(WebSocketContext wsContext) {
        Output output = new Output();
        MudCharacterTemplate npcTemplate = getRepositoryBundle().getCharacterPrototypeRepository().findById((Long)wsContext.getAttributes().get(MEDIT_MODEL)).orElseThrow();

        if (!wsContext.getAttributes().containsKey(MEDIT_STATE)) {
            populateMenuItems(npcTemplate);
            return menuPane.render(Color.DYELLOW, Color.DWHITE);
        }

        MeditState meditState = (MeditState)wsContext.getAttributes().get(MEDIT_STATE);

        switch (meditState) {
            case NAME -> output.append("[green]New name: ");
            case HEARTS -> output.append("[green]How many hearts: ");
        }

        return output;
    }

    @Override
    public Response answer(WebSocketContext webSocketContext, Input input) {
        String nextQuestion = "nonPlayerCreatureEditorQuestion";
        Output output = new Output();
        MudCharacter ch = getCharacter(webSocketContext, output).orElseThrow();
        MudCharacterTemplate npcTemplate = getRepositoryBundle().getCharacterPrototypeRepository().findById((Long)webSocketContext.getAttributes().get(MEDIT_MODEL)).orElseThrow();

        if (!webSocketContext.getAttributes().containsKey(MEDIT_STATE)) {
            String choice = input.getInput().toUpperCase(Locale.ROOT);

            switch (choice) {
                case "N" -> webSocketContext.getAttributes().put(MEDIT_STATE, MeditState.NAME);
                case "P" -> nextQuestion = "nonPlayerCreaturePronounEditorQuestion";
                case "H" -> webSocketContext.getAttributes().put(MEDIT_STATE, MeditState.HEARTS);
                case "X" -> {
                    getRepositoryBundle().getCharacterPrototypeRepository().save(npcTemplate);
                    output.append("[green]Saved changes.");

                    webSocketContext.getAttributes().remove(MEDIT_STATE);
                    webSocketContext.getAttributes().remove(MEDIT_MODEL);
                    nextQuestion = "commandQuestion";

                    commService.sendToRoom(ch.getLocation().getRoom().getId(),
                        new Output("[yellow]%s stops editing.", ch.getCharacter().getName()), ch);
                }
            }
        } else {
            MeditState meditState = (MeditState)webSocketContext.getAttributes().get(MEDIT_STATE);

            switch (meditState) {
                case NAME -> {
                    npcTemplate.getCharacter().setName(input.getInput());
                    webSocketContext.getAttributes().remove(MEDIT_STATE);
                }
                case HEARTS -> {
                    try {
                        int count = Integer.parseInt(input.getInput());

                        if (count < 1 || count > 4) {
                            output.append("[red]Choose a number of hearts from 1 to 4.");
                        } else {
                            npcTemplate.getCharacter().setHitPoints(count * 10);
                            npcTemplate.getCharacter().setMaxHitPoints(count * 10);
                            webSocketContext.getAttributes().remove(MEDIT_STATE);
                        }
                    } catch (NumberFormatException e) {
                        output.append("[red]Choose a number of hearts from 1 to 4.");
                    }
                }
            }
        }

        Question next = getQuestion(nextQuestion);

        return new Response(next, output);
    }

    private void populateMenuItems(MudCharacterTemplate npcTemplate) {
        menuPane.getItems().clear();

        menuPane.getTitle().setTitle(String.format("Non-Player Character Editor - %d", npcTemplate.getId()));
        menuPane.getItems().add(new MenuItem("N", "Name: " + npcTemplate.getCharacter().getName()));
        menuPane.getItems().add(new MenuItem("P", "Pronoun: " + npcTemplate.getCharacter().getPronoun()));
        menuPane.getItems().add(new MenuItem("H", "Hearts: [red]" + CharacterSheetFormatter.hearts(npcTemplate)));

        menuPane.getItems().add(new MenuItem("X", "Save"));
    }
}
