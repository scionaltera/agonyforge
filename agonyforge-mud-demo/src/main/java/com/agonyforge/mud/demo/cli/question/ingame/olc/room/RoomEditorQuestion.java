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
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudRoom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
public class RoomEditorQuestion extends BaseQuestion {
    static final String REDIT_STATE = "REDIT.STATE";
    static final String REDIT_MODEL = "REDIT.MODEL";

    private static final Logger LOGGER = LoggerFactory.getLogger(RoomEditorQuestion.class);

    private final MenuPane menuPane = new MenuPane();

    public RoomEditorQuestion(ApplicationContext applicationContext,
                              RepositoryBundle repositoryBundle) {
        super(applicationContext, repositoryBundle);

        menuPane.setTitle(new MenuTitle("Room Editor"));
        menuPane.setPrompt(new MenuPrompt());
    }

    @Override
    public Output prompt(WebSocketContext wsContext) {
        Output output = new Output();
        MudCharacter ch = getCharacter(wsContext, output, false).orElseThrow();
        MudRoom room = getRoomModel(wsContext, ch);

        if (!wsContext.getAttributes().containsKey(REDIT_STATE)) {
            populateMenuItems(room);
            return menuPane.render(Color.GREEN, Color.CYAN);
        } else if ("ROOM.TITLE".equals(wsContext.getAttributes().get(REDIT_STATE))) {
            output.append("[green]New title: ");
        } else if ("ROOM.DESCRIPTION".equals(wsContext.getAttributes().get(REDIT_STATE))) {
            output.append("[green]New description: ");
        }

        return output;
    }

    @Override
    public Response answer(WebSocketContext wsContext, Input input) {
        String nextQuestion = "roomEditorQuestion";
        Output output = new Output();
        MudCharacter ch = getCharacter(wsContext, output, false).orElseThrow();
        MudRoom room = getRoomModel(wsContext, ch);

        if (!wsContext.getAttributes().containsKey(REDIT_STATE)) {
            String choice = input.getInput().toUpperCase(Locale.ROOT);

            switch (choice) {
                case "T" -> wsContext.getAttributes().put(REDIT_STATE, "ROOM.TITLE");
                case "D" -> wsContext.getAttributes().put(REDIT_STATE, "ROOM.DESCRIPTION");
                case "E" -> {
//                    output.append("[green]Editing exits...");
                    nextQuestion = "roomExitsQuestion";
                }
                case "S" -> {
                    getRepositoryBundle().getRoomRepository().save(room);
                    output.append("[green]Saved changes.");

                    wsContext.getAttributes().remove(REDIT_STATE);
                    wsContext.getAttributes().remove(REDIT_MODEL);
                    nextQuestion = "commandQuestion";
                }
                case "Q" -> {
                    output.append("[red]Abandoned changes.");

                    wsContext.getAttributes().remove(REDIT_STATE);
                    wsContext.getAttributes().remove(REDIT_MODEL);
                    nextQuestion = "commandQuestion";
                }
            }
        } else if ("ROOM.TITLE".equals(wsContext.getAttributes().get(REDIT_STATE))) {
            room.setName(input.getInput());
            wsContext.getAttributes().remove(REDIT_STATE);
        } else if ("ROOM.DESCRIPTION".equals(wsContext.getAttributes().get(REDIT_STATE))) {
            room.setDescription(input.getInput());
            wsContext.getAttributes().remove(REDIT_STATE);
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

        menuPane.getItems().add(new MenuItem("T", "Title: " + room.getName()));
        menuPane.getItems().add(new MenuItem("D", "Description: " + room.getDescription()));
        menuPane.getItems().add(new MenuItem("E", "Exits"));

        menuPane.getItems().add(new MenuItem("S", "Save"));
        menuPane.getItems().add(new MenuItem("Q", "Quit without saving"));
    }
}
