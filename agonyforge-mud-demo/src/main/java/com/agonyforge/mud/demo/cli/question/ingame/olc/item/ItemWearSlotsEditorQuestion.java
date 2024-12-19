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
import com.agonyforge.mud.demo.model.constant.WearSlot;
import com.agonyforge.mud.demo.model.impl.MudItemPrototype;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.agonyforge.mud.demo.cli.question.ingame.olc.item.ItemEditorQuestion.IEDIT_STATE;

@Component
public class ItemWearSlotsEditorQuestion extends BaseQuestion {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemWearSlotsEditorQuestion.class);

    static final String IEDIT_SLOT = "IEDIT.WEAR_SLOT";

    private final MenuPane menuPane = new MenuPane();

    public ItemWearSlotsEditorQuestion(ApplicationContext applicationContext, RepositoryBundle repositoryBundle) {
        super(applicationContext, repositoryBundle);

        menuPane.setTitle(new MenuTitle("Item Wear Slots"));
        menuPane.setPrompt(new MenuPrompt());
    }

    @Override
    public Output prompt(WebSocketContext wsContext) {
        MudItemPrototype itemProto = getRepositoryBundle().getItemPrototypeRepository().findById((Long)wsContext.getAttributes().get("IEDIT.MODEL")).orElseThrow();

        if (wsContext.getAttributes().containsKey(IEDIT_STATE) && wsContext.getAttributes().get(IEDIT_STATE).toString().equals(IEDIT_SLOT)) {
            WearSlot slot = (WearSlot)wsContext.getAttributes().get(IEDIT_SLOT);

            if (itemProto.getWearSlots().contains(slot)) {
                itemProto.getWearSlots().remove(slot);
            } else {
                itemProto.getWearSlots().add(slot);
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
        } else {
            Optional<MenuItem> itemOptional = menuPane.getItems().stream()
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

    void populateMenuItems(MudItemPrototype item) {
        menuPane.getItems().clear();

        List<MenuItem> menuItems = Arrays.stream(WearSlot.values())
            .sequential()
            .map(slot -> new MenuItem(
                String.format("%-2d", slot.ordinal()),
                String.format("%-15s [%s]%s", slot.getName(), item.getWearSlots().contains(slot) ? "green" : "red", item.getWearSlots().contains(slot)),
                slot))
                .toList();

        menuPane.getItems().addAll(menuItems);

        menuPane.getItems().add(new MenuItem("X ", "Exit"));
    }
}
