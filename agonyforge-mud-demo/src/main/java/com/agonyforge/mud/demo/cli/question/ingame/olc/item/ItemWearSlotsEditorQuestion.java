package com.agonyforge.mud.demo.cli.question.ingame.olc.item;

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
import com.agonyforge.mud.demo.model.constant.WearMode;
import com.agonyforge.mud.demo.model.constant.WearSlot;
import com.agonyforge.mud.demo.model.impl.MudItemTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.agonyforge.mud.demo.cli.question.ingame.olc.item.ItemEditorQuestion.IEDIT_STATE;

@Component
public class ItemWearSlotsEditorQuestion extends BaseQuestion {
    static final String IEDIT_SLOT = "IEDIT.WEAR_SLOT";
    static final String IEDIT_MODE = "IEDIT.WEAR_MODE";

    private final MenuPane menuPane = new MenuPane();

    public ItemWearSlotsEditorQuestion(ApplicationContext applicationContext, RepositoryBundle repositoryBundle) {
        super(applicationContext, repositoryBundle);

        menuPane.setTitle(new MenuTitle("Item Wear Slots"));
        menuPane.setPrompt(new MenuPrompt());
    }

    @Override
    public Output prompt(WebSocketContext wsContext) {
        MudItemTemplate itemProto = getRepositoryBundle().getItemPrototypeRepository().findById((Long)wsContext.getAttributes().get("IEDIT.MODEL")).orElseThrow();

        if (wsContext.getAttributes().containsKey(IEDIT_STATE)) {
            if (wsContext.getAttributes().get(IEDIT_STATE).toString().equals(IEDIT_SLOT)) {
                WearSlot slot = (WearSlot) wsContext.getAttributes().get(IEDIT_SLOT);

                if (itemProto.getItem().getWearSlots().contains(slot)) {
                    itemProto.getItem().getWearSlots().remove(slot);
                } else {
                    itemProto.getItem().getWearSlots().add(slot);
                }

                wsContext.getAttributes().remove(IEDIT_STATE, IEDIT_SLOT);
            } else if (wsContext.getAttributes().get(IEDIT_STATE).toString().equals(IEDIT_MODE)) {
                WearMode mode = (WearMode) wsContext.getAttributes().get(IEDIT_MODE);

                itemProto.getItem().setWearMode(mode);
                wsContext.getAttributes().remove(IEDIT_STATE, IEDIT_MODE);
            }

            itemProto = getRepositoryBundle().getItemPrototypeRepository().save(itemProto);
        }

        populateMenuItems(itemProto);
        return menuPane.render(Color.DYELLOW, Color.DWHITE);
    }

    @Override
    public Response answer(WebSocketContext wsContext, Input input) {
        String nextQuestion = "itemWearSlotsEditorQuestion";
        Output output = new Output();
        String choice = input.getInput().toUpperCase(Locale.ROOT);

        if ("X".equals(choice)) {
            nextQuestion = "itemEditorQuestion";
            wsContext.getAttributes().remove(IEDIT_STATE);
            wsContext.getAttributes().remove(IEDIT_SLOT);
        } else if ("M".equals(choice)) {
            MenuItem menuItem = menuPane.getItems()
                .stream()
                .filter(item -> choice.equals(item.getKey().trim()))
                .map(item -> (MenuItem)item)
                .findFirst().orElseThrow();

            wsContext.getAttributes().put(IEDIT_STATE, IEDIT_MODE);
            wsContext.getAttributes().put(IEDIT_MODE, menuItem.getItem());
        } else {
            Optional<MenuItem> itemOptional = menuPane.getItems()
                .stream()
                .filter(item -> choice.equals(item.getKey().trim()))
                .map(item -> (MenuItem)item)
                .findFirst();

            if (itemOptional.isPresent()) {
                MenuItem menuItem = itemOptional.get();
                WearSlot slot = (WearSlot)menuItem.getItem();

                wsContext.getAttributes().put(IEDIT_STATE, IEDIT_SLOT);
                wsContext.getAttributes().put(IEDIT_SLOT, slot);
            } else {
                output.append("[red]Please choose a menu item.");
            }
        }

        Question next = getQuestion(nextQuestion);

        return new Response(next, output);
    }

    void populateMenuItems(MudItemTemplate item) {
        menuPane.getItems().clear();

        List<MenuItem> menuItems = Arrays.stream(WearSlot.values())
            .sequential()
            .map(slot -> new MenuItem(
                String.format("%-2d", slot.ordinal()),
                String.format("%-15s [%s]%s", slot.getName(), item.getItem().getWearSlots().contains(slot) ? "green" : "red", item.getItem().getWearSlots().contains(slot)),
                slot))
                .toList();

        menuPane.getItems().addAll(menuItems);

        menuPane.getItems().add(new MenuItem("M ", String.format("Wear Slot Mode: [green]%s", item.getItem().getWearMode()), item.getItem().getWearMode() == WearMode.ALL ? WearMode.SINGLE : WearMode.ALL));
        menuPane.getItems().add(new MenuItem("X ", "Exit"));
    }
}
