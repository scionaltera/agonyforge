package com.agonyforge.mud.demo.cli.question.ingame.olc.room;

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
import com.agonyforge.mud.demo.model.constant.RoomFlag;
import com.agonyforge.mud.demo.model.impl.MudRoom;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.agonyforge.mud.demo.cli.question.ingame.olc.room.RoomEditorQuestion.REDIT_STATE;

@Component
public class RoomFlagsEditorQuestion extends BaseQuestion {
    private static final String REDIT_FLAG = "REDIT.FLAG";

    private final MenuPane menuPane = new MenuPane();

    public RoomFlagsEditorQuestion(ApplicationContext applicationContext, RepositoryBundle repositoryBundle) {
        super(applicationContext, repositoryBundle);

        menuPane.setTitle(new MenuTitle("Room Flags"));
        menuPane.setPrompt(new MenuPrompt());
    }

    @Override
    public Output prompt(WebSocketContext wsContext) {
        MudRoom room = (MudRoom)wsContext.getAttributes().get("REDIT.MODEL");

        if (wsContext.getAttributes().containsKey(REDIT_STATE)) {
            if (wsContext.getAttributes().get(REDIT_STATE).toString().equals(REDIT_FLAG)) {
                RoomFlag flag = (RoomFlag) wsContext.getAttributes().get(REDIT_FLAG);

                if (room.getFlags().contains(flag)) {
                    room.getFlags().remove(flag);
                } else {
                    room.getFlags().add(flag);
                }

                wsContext.getAttributes().remove(REDIT_STATE, REDIT_FLAG);
            }

            room = getRepositoryBundle().getRoomRepository().save(room);
        }

        populateMenuItems(room);
        return menuPane.render(Color.DYELLOW, Color.DWHITE);
    }

    @Override
    public Response answer(WebSocketContext wsContext, Input input) {
        String nextQuestion = "roomFlagsEditorQuestion";
        Output output = new Output();
        String choice = input.getInput().toUpperCase(Locale.ROOT);

        if ("X".equals(choice)) {
            nextQuestion = "roomEditorQuestion";
            wsContext.getAttributes().remove(REDIT_STATE);
            wsContext.getAttributes().remove(REDIT_FLAG);
        } else {
            Optional<MenuItem> itemOptional = menuPane.getItems()
                .stream()
                .filter(item -> choice.equals(item.getKey().trim()))
                .map(item -> (MenuItem)item)
                .findFirst();

            if (itemOptional.isPresent()) {
                MenuItem menuItem = itemOptional.get();
                RoomFlag flag = (RoomFlag)menuItem.getItem();

                wsContext.getAttributes().put(REDIT_STATE, REDIT_FLAG);
                wsContext.getAttributes().put(REDIT_FLAG, flag);
            } else {
                output.append("[red]Please choose a menu item.");
            }
        }

        Question next = getQuestion(nextQuestion);

        return new Response(next, output);
    }

    void populateMenuItems(MudRoom room) {
        menuPane.getItems().clear();

        List<MenuItem> menuItems = Arrays.stream(RoomFlag.values())
            .sequential()
            .map(flag -> new MenuItem(
                String.format("%-2d", flag.ordinal()),
                String.format("%-10s: ([%s]%-5s[dyellow]) %s",
                    flag.name(),
                    room.getFlags().contains(flag) ? "green" : "red",
                    room.getFlags().contains(flag),
                    flag.getDescription()
                    ),
                flag))
            .toList();

        menuPane.getItems().addAll(menuItems);
        menuPane.getItems().add(new MenuItem("X ", "Exit"));
    }
}
