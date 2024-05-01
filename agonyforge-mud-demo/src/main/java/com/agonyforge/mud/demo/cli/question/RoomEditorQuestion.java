package com.agonyforge.mud.demo.cli.question;

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
import com.agonyforge.mud.demo.model.impl.MudCharacter;
<<<<<<< Updated upstream
<<<<<<< Updated upstream
<<<<<<< Updated upstream
<<<<<<< Updated upstream
=======
import com.agonyforge.mud.demo.model.impl.MudRoom;
>>>>>>> Stashed changes
=======
import com.agonyforge.mud.demo.model.impl.MudRoom;
>>>>>>> Stashed changes
=======
import com.agonyforge.mud.demo.model.impl.MudRoom;
>>>>>>> Stashed changes
=======
import com.agonyforge.mud.demo.model.impl.MudRoom;
>>>>>>> Stashed changes
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class RoomEditorQuestion extends BaseQuestion {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomEditorQuestion.class);
<<<<<<< Updated upstream
<<<<<<< Updated upstream
<<<<<<< Updated upstream
<<<<<<< Updated upstream
=======
    private static final String REDIT_STATE = "REDIT.STATE";
>>>>>>> Stashed changes
=======
    private static final String REDIT_STATE = "REDIT.STATE";
>>>>>>> Stashed changes
=======
    private static final String REDIT_STATE = "REDIT.STATE";
>>>>>>> Stashed changes
=======
    private static final String REDIT_STATE = "REDIT.STATE";
>>>>>>> Stashed changes

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
<<<<<<< Updated upstream
<<<<<<< Updated upstream
<<<<<<< Updated upstream
<<<<<<< Updated upstream
        MudCharacter ch = getCharacter(wsContext, output).orElseThrow();
=======
        MudCharacter ch = getCharacter(wsContext, output, false).orElseThrow();
        MudRoom room = getRepositoryBundle().getRoomRepository().getById(ch.getRoomId()).orElseThrow();
>>>>>>> Stashed changes

        if (!wsContext.getAttributes().containsKey(REDIT_STATE)) {
            populateMenuItems(ch, room);
            return menuPane.render(Color.YELLOW, Color.GREEN);
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
        MudRoom room = getRepositoryBundle().getRoomRepository().getById(ch.getRoomId()).orElseThrow();

<<<<<<< Updated upstream
        if (choice.equals("S")) {
            output.append("[green]Room saved!");
            nextQuestion = "commandQuestion";
=======
=======
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
        MudCharacter ch = getCharacter(wsContext, output, false).orElseThrow();
        MudRoom room = getRepositoryBundle().getRoomRepository().getById(ch.getRoomId()).orElseThrow();

        if (!wsContext.getAttributes().containsKey(REDIT_STATE)) {
            populateMenuItems(ch, room);
            return menuPane.render(Color.YELLOW, Color.GREEN);
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
        MudRoom room = getRepositoryBundle().getRoomRepository().getById(ch.getRoomId()).orElseThrow();

=======
>>>>>>> Stashed changes
        if (!wsContext.getAttributes().containsKey(REDIT_STATE)) {
            String choice = input.getInput().toUpperCase(Locale.ROOT);

            switch (choice) {
                case "T" -> {
                    output.append("[green]Editing title: ");
                    wsContext.getAttributes().put(REDIT_STATE, "ROOM.TITLE");
                }
                case "D" -> {
                    output.append("[green]Editing description: ");
                    wsContext.getAttributes().put(REDIT_STATE, "ROOM.DESCRIPTION");
                }
                case "E" -> {
                    output.append("[green]Exiting...");
                    nextQuestion = "commandQuestion";
                    wsContext.getAttributes().remove(REDIT_STATE);
                }
            }
        } else if ("ROOM.TITLE".equals(wsContext.getAttributes().get(REDIT_STATE))) {
            room.setName(input.getInput());
            getRepositoryBundle().getRoomRepository().save(room);
            wsContext.getAttributes().remove(REDIT_STATE);
        } else if ("ROOM.DESCRIPTION".equals(wsContext.getAttributes().get(REDIT_STATE))) {
            room.setDescription(input.getInput());
            getRepositoryBundle().getRoomRepository().save(room);
            wsContext.getAttributes().remove(REDIT_STATE);
<<<<<<< Updated upstream
<<<<<<< Updated upstream
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
        }

        Question next = getQuestion(nextQuestion);

        return new Response(next, output);
    }

<<<<<<< Updated upstream
<<<<<<< Updated upstream
<<<<<<< Updated upstream
<<<<<<< Updated upstream
    private void populateMenuItems(MudCharacter ch) {
        menuPane.getItems().clear();

        menuPane.getItems().add(new MenuItem("S", "Save"));
=======
=======
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
    private void populateMenuItems(MudCharacter ch, MudRoom room) {
        menuPane.getItems().clear();

=======
    private void populateMenuItems(MudCharacter ch, MudRoom room) {
        menuPane.getItems().clear();

>>>>>>> Stashed changes
        menuPane.getItems().add(new MenuItem("T", "Title: " + room.getName()));
        menuPane.getItems().add(new MenuItem("D", "Description: " + room.getDescription()));

        menuPane.getItems().add(new MenuItem("E", "Exit"));
<<<<<<< Updated upstream
<<<<<<< Updated upstream
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
    }
}
