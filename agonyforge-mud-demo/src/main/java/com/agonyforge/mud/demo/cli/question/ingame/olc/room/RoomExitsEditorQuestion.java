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
import com.agonyforge.mud.demo.model.constant.Direction;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudRoom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.Optional;

import static com.agonyforge.mud.demo.cli.question.ingame.olc.room.RoomEditorQuestion.REDIT_MODEL;
import static com.agonyforge.mud.demo.cli.question.ingame.olc.room.RoomEditorQuestion.REDIT_STATE;

@Component
public class RoomExitsEditorQuestion extends BaseQuestion {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomExitsEditorQuestion.class);

    // REDIT.EXIT holds the direction of the exit we are editing. The user picks a direction
    // from the menu, and is asked for a room number to assign that direction to. When they
    // type in the number we need both that and the direction in order to link up the exit.
    static final String REDIT_EXIT = "REDIT.EXIT";

    private final MenuPane menuPane = new MenuPane();

    public RoomExitsEditorQuestion(ApplicationContext applicationContext,
                                   RepositoryBundle repositoryBundle) {
        super(applicationContext, repositoryBundle);

        menuPane.setTitle(new MenuTitle("Room Exits"));
        menuPane.setPrompt(new MenuPrompt());
    }

    @Override
    public Output prompt(WebSocketContext wsContext) {
        Output output = new Output();
        MudCharacter ch = getCharacter(wsContext, output).orElseThrow();
        MudRoom room = getRoomModel(wsContext, ch);

        if (wsContext.getAttributes().containsKey(REDIT_STATE) && wsContext.getAttributes().get(REDIT_STATE).toString().startsWith("ROOM.EXITS")) {
            output.append("Destination room ID for exit %s, or 0 to delete: ", wsContext.getAttributes().get(REDIT_EXIT));
            return output;
        } else {
            populateMenuItems(room);
            return menuPane.render(Color.DYELLOW, Color.DWHITE);
        }
    }

    @Override
    public Response answer(WebSocketContext wsContext, Input input) {
        String nextQuestion = "roomExitsQuestion";
        Output output = new Output();
        MudCharacter ch = getCharacter(wsContext, output).orElseThrow();
        MudRoom room = getRoomModel(wsContext, ch);

        if (wsContext.getAttributes().containsKey(REDIT_EXIT)) {
            try {
                Direction dir = (Direction) wsContext.getAttributes().get(REDIT_EXIT);
                long choice = Long.parseLong(input.getInput());

                if (choice == 0L) {
                    room.removeExit(dir.getName());
                    output.append("[red]Removed exit: %s", dir);
                } else {
                    Optional<MudRoom> destinationOptional = getRepositoryBundle().getRoomRepository().findById(choice);

                    if (destinationOptional.isPresent()) {
                        room.setExit(dir.getName(), new MudRoom.Exit(choice));
                        output.append("[green]Updated exit: %s -> %d", dir, choice);
                    } else {
                        output.append("[red]Room %d doesn't exist. Please create it first.", choice);
                    }
                }

                wsContext.getAttributes().remove(REDIT_STATE);
                wsContext.getAttributes().remove(REDIT_EXIT);
            } catch (NumberFormatException e) {
                output.append("[red]Please enter a room number, or 0 to delete the exit.");
            }
        } else {
            String choice = input.getInput().toUpperCase(Locale.ROOT).substring(0, 1);

            if ("X".equals(choice)) {
                nextQuestion = "roomEditorQuestion";
                wsContext.getAttributes().remove(REDIT_STATE);
                wsContext.getAttributes().remove(REDIT_EXIT);
            } else {
                Optional<MenuItem> itemOptional = menuPane.getItems().stream()
                    .filter(item -> choice.equals(item.getKey()))
                    .map(item -> (MenuItem)item)
                    .findFirst();

                if (itemOptional.isPresent()) {
                    MenuItem item = itemOptional.get();
                    Direction direction = (Direction)item.getItem();

                    wsContext.getAttributes().put(REDIT_STATE, "ROOM.EXITS");
                    wsContext.getAttributes().put(REDIT_EXIT, direction);
                } else {
                    output.append("[red]Please choose a menu item.");
                }
            }
        }

        Question next = getQuestion(nextQuestion);

        return new Response(next, output);
    }

    private MudRoom getRoomModel(WebSocketContext wsContext, MudCharacter ch) {
        if (!wsContext.getAttributes().containsKey(REDIT_MODEL)) {
            wsContext.getAttributes().put(REDIT_MODEL, ch.getRoomId());
        }

        return getRepositoryBundle().getRoomRepository().findById(ch.getRoomId()).orElseThrow();
    }

    void populateMenuItems(MudRoom room) {
        menuPane.getItems().clear();

        for (int i = 0; i < Direction.values().length; i++) {
            Direction direction = Direction.values()[i];
            MudRoom.Exit exit = room.getExit(direction.getName());

            MenuItem item = new MenuItem(
                Integer.toString(i + 1),
                String.format("%s %s",
                    StringUtils.capitalize(direction.getName()),
                    exit != null ? "to " + exit.getDestinationId() : ""),
                direction
            );

            menuPane.getItems().add(item);
        }

        menuPane.getItems().add(new MenuItem("X", "Exit"));
    }
}
