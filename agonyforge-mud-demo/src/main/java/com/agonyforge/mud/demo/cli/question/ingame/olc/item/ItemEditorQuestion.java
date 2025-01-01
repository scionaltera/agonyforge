package com.agonyforge.mud.demo.cli.question.ingame.olc.item;

import com.agonyforge.mud.core.cli.Color;
import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.cli.Tokenizer;
import com.agonyforge.mud.core.cli.menu.impl.MenuItem;
import com.agonyforge.mud.core.cli.menu.impl.MenuPane;
import com.agonyforge.mud.core.cli.menu.impl.MenuPrompt;
import com.agonyforge.mud.core.cli.menu.impl.MenuTitle;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.cli.question.BaseQuestion;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudItemPrototype;
import com.agonyforge.mud.demo.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Locale;

@Component
public class ItemEditorQuestion extends BaseQuestion {
    // IEDIT.MODEL holds the ID of the item prototype we are editing.
    public static final String IEDIT_MODEL = "IEDIT.MODEL";

    // TODO this could be an enum
    // IEDIT.STATE holds the current state of the editor:
    // empty -> user needs to select a menu item
    // IEDIT.NAMES -> user needs to type a name list
    // IEDIT.SHORT_DESC -> user needs to type a short description
    // IEDIT.LONG_DESC -> user needs to type a long description
    // IEDIT.WEAR_SLOT -> user needs to pick a wear slot
    static final String IEDIT_STATE = "IEDIT.STATE";

    private final CommService commService;
    private final MenuPane menuPane = new MenuPane();

    @Autowired
    public ItemEditorQuestion(ApplicationContext applicationContext,
                              RepositoryBundle repositoryBundle,
                              CommService commService) {
        super(applicationContext, repositoryBundle);

        this.commService = commService;

        menuPane.setTitle(new MenuTitle("Item Editor"));
        menuPane.setPrompt(new MenuPrompt());
    }

    @Override
    public Output prompt(WebSocketContext wsContext) {
        Output output = new Output();
        MudItemPrototype itemProto = getRepositoryBundle().getItemPrototypeRepository().findById((Long)wsContext.getAttributes().get(IEDIT_MODEL)).orElseThrow();

        if (!wsContext.getAttributes().containsKey(IEDIT_STATE)) {
            populateMenuItems(itemProto);
            return menuPane.render(Color.DYELLOW, Color.DWHITE);
        } else if ("IEDIT.NAMES".equals(wsContext.getAttributes().get(IEDIT_STATE))) {
            output.append("[green]New name list: ");
        } else if ("IEDIT.SHORT_DESC".equals(wsContext.getAttributes().get(IEDIT_STATE))) {
            output.append("[green]New short description: ");
        } else if ("IEDIT.LONG_DESC".equals(wsContext.getAttributes().get(IEDIT_STATE))) {
            output.append("[green]New long description: ");
        }

        return output;
    }

    @Override
    public Response answer(WebSocketContext wsContext, Input input) {
        String nextQuestion = "itemEditorQuestion";
        Output output = new Output();
        MudCharacter ch = getCharacter(wsContext, output).orElseThrow();
        MudItemPrototype itemProto = getRepositoryBundle().getItemPrototypeRepository().findById((Long)wsContext.getAttributes().get(IEDIT_MODEL)).orElseThrow();

        if (!wsContext.getAttributes().containsKey(IEDIT_STATE)) {
            String choice = input.getInput().toUpperCase(Locale.ROOT);

            switch (choice) {
                case "N" -> wsContext.getAttributes().put(IEDIT_STATE, "IEDIT.NAMES");
                case "S" -> wsContext.getAttributes().put(IEDIT_STATE, "IEDIT.SHORT_DESC");
                case "L" -> wsContext.getAttributes().put(IEDIT_STATE, "IEDIT.LONG_DESC");
                case "W" -> nextQuestion = "itemWearSlotsEditorQuestion";
                case "X" -> {
                    getRepositoryBundle().getItemPrototypeRepository().save(itemProto);
                    output.append("[green]Saved changes.");

                    wsContext.getAttributes().remove(IEDIT_STATE);
                    wsContext.getAttributes().remove(IEDIT_MODEL);
                    nextQuestion = "commandQuestion";

                    commService.sendToRoom(wsContext, ch.getRoomId(),
                        new Output("[yellow]%s stops editing.", ch.getCharacter().getName()), ch);
                }
            }
        } else if ("IEDIT.NAMES".equals(wsContext.getAttributes().get(IEDIT_STATE))) {
            itemProto.getItem().setNameList(new HashSet<>(Tokenizer.tokenize(input.getInput())));
            wsContext.getAttributes().remove(IEDIT_STATE);
        } else if ("IEDIT.SHORT_DESC".equals(wsContext.getAttributes().get(IEDIT_STATE))) {
            itemProto.getItem().setShortDescription(input.getInput());
            wsContext.getAttributes().remove(IEDIT_STATE);
        } else if ("IEDIT.LONG_DESC".equals(wsContext.getAttributes().get(IEDIT_STATE))) {
            itemProto.getItem().setLongDescription(input.getInput());
            wsContext.getAttributes().remove(IEDIT_STATE);
        }

        Question next = getQuestion(nextQuestion);

        return new Response(next, output);
    }

    private void populateMenuItems(MudItemPrototype itemProto) {
        menuPane.getItems().clear();

        menuPane.getTitle().setTitle(String.format("Item Editor - %d", itemProto.getId()));
        menuPane.getItems().add(new MenuItem("N", "Name List: " + itemProto.getItem().getNameList()));
        menuPane.getItems().add(new MenuItem("S", "Short Description: " + itemProto.getItem().getShortDescription()));
        menuPane.getItems().add(new MenuItem("L", "Long Description: " + itemProto.getItem().getLongDescription()));
        menuPane.getItems().add(new MenuItem("W", "Wear Slots"));

        menuPane.getItems().add(new MenuItem("X", "Save"));
    }
}
