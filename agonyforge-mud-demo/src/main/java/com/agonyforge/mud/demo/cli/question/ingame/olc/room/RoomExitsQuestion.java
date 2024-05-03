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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.agonyforge.mud.demo.cli.question.ingame.olc.room.RoomEditorQuestion.REDIT_MODEL;
import static com.agonyforge.mud.demo.cli.question.ingame.olc.room.RoomEditorQuestion.REDIT_STATE;
import static java.util.stream.Collectors.toList;

@Component
public class RoomExitsQuestion extends BaseQuestion {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomExitsQuestion.class);
    private static final String REDIT_EXIT = "REDIT.EXIT";

    private final MenuPane menuPane = new MenuPane();
    private final Map<String, Direction> directionMap = new HashMap<>();

    public RoomExitsQuestion(ApplicationContext applicationContext,
                             RepositoryBundle repositoryBundle) {
        super(applicationContext, repositoryBundle);

        menuPane.setTitle(new MenuTitle("Room Exits"));
        menuPane.setPrompt(new MenuPrompt());

        Arrays.stream(Direction.values())
            .forEach(direction -> directionMap.put(direction.name().substring(0, 1), direction));
    }

    @Override
    public Output prompt(WebSocketContext wsContext) {
        Output output = new Output();
        MudCharacter ch = getCharacter(wsContext, output, false).orElseThrow();
        MudRoom room = getRoomModel(wsContext, ch);

        if (wsContext.getAttributes().containsKey(REDIT_STATE) && wsContext.getAttributes().get(REDIT_STATE).toString().startsWith("ROOM.EXITS")) {
            output.append("Destination for exit %s: ", wsContext.getAttributes().get(REDIT_EXIT));
            return output;
        } else {
            populateMenuItems(room);
            return menuPane.render(Color.GREEN, Color.CYAN);
        }
    }

    @Override
    public Response answer(WebSocketContext wsContext, Input input) {
        String nextQuestion = "roomExitsQuestion";
        Output output = new Output();
        MudCharacter ch = getCharacter(wsContext, output, false).orElseThrow();
        MudRoom room = getRoomModel(wsContext, ch);

        if (wsContext.getAttributes().containsKey(REDIT_EXIT)) {
            try {
                Direction dir = (Direction) wsContext.getAttributes().get(REDIT_EXIT);
                long choice = Long.parseLong(input.getInput());

                if (choice == 0L) {
                    room.removeExit(dir.getName());
                    output.append("[red]Removed exit: %s", dir);
                } else {
                    room.setExit(dir.getName(), new MudRoom.Exit(choice));
                    output.append("[green]Updated exit: %s -> %d", dir, choice);
                }

                wsContext.getAttributes().remove(REDIT_STATE);
                wsContext.getAttributes().remove(REDIT_EXIT);
            } catch (NumberFormatException e) {
                output.append("[red]Please enter a room number, or 0.");
            }
        } else {
            String choice = input.getInput().toUpperCase(Locale.ROOT).substring(0, 1);

            if ("Q".equals(choice)) {
                nextQuestion = "roomEditorQuestion";
            } else if (directionMap.containsKey(choice)) {
                Direction dir = directionMap.get(choice);
                wsContext.getAttributes().put(REDIT_STATE, "ROOM.EXITS");
                wsContext.getAttributes().put(REDIT_EXIT, dir);
            }
        }

        Question next = getQuestion(nextQuestion);

        return new Response(next, output);
    }

    private MudRoom getRoomModel(WebSocketContext wsContext, MudCharacter ch) {
        if (!wsContext.getAttributes().containsKey(REDIT_MODEL)) {
            MudRoom room = getRepositoryBundle().getRoomRepository().getById(ch.getRoomId()).orElseThrow();
            wsContext.getAttributes().put(REDIT_MODEL, room);
        }

        return (MudRoom)wsContext.getAttributes().get(REDIT_MODEL);
    }

    private void populateMenuItems(MudRoom room) {
        menuPane.getItems().clear();

        List<MenuItem> menuItems = Arrays.stream(Direction.values())
            .map(direction -> {
                MudRoom.Exit exit = room.getExit(direction.getName());

                return new MenuItem(
                    direction.getName().toUpperCase(Locale.ROOT).substring(0, 1),
                    String.format("%s %s",
                        StringUtils.capitalize(direction.getName()),
                        exit != null ? "to " + exit.getDestinationId() : "")
                );
            })
            .toList();

        menuPane.getItems().addAll(menuItems);
        menuPane.getItems().add(new MenuItem("Q", "Quit"));
    }
}
