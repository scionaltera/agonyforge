package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.CharacterComponent;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudCharacterTemplate;
import com.agonyforge.mud.demo.model.impl.NonPlayerComponent;
import com.agonyforge.mud.demo.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.agonyforge.mud.demo.cli.question.ingame.olc.creature.NonPlayerCreatureEditorQuestion.MEDIT_MODEL;

@Component
public class NonPlayerCreatureEditorCommand extends AbstractCommand {
    @Autowired
    public NonPlayerCreatureEditorCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);

        // MEDIT <number> <-- edit a specific NPC by number (existing or new)
        // MEDIT <name>   <-- edit an NPC in the room you're standing in

        if (tokens.size() != 2) {
            output
                .append("Expected one argument.", tokens)
                .append("[default]Valid arguments:")
                .append("MEDIT &lt;NPC number&gt;")
                .append("MEDIT &lt;NPC name in same room&gt;");
            return question;
        }

        Optional<MudCharacterTemplate> npcTemplateOptional = findNpcToEdit(ch, tokens.get(1));

        if (npcTemplateOptional.isEmpty()) {
            try {
                Long id = Long.parseLong(tokens.get(1));
                MudCharacterTemplate npcTemplate = new MudCharacterTemplate();

                npcTemplate.setId(id);
                npcTemplate.setCharacter(new CharacterComponent());
                npcTemplate.setNonPlayer(new NonPlayerComponent());

                npcTemplate = getRepositoryBundle().getCharacterPrototypeRepository().save(npcTemplate);

                npcTemplateOptional = Optional.of(npcTemplate);
            } catch (NumberFormatException e) {
                output.append("[red]Unable to find or create requested creature prototype.");
                return question;
            }
        }

        webSocketContext.getAttributes().put(MEDIT_MODEL, npcTemplateOptional.get().getId());
        getCommService().sendToRoom(
            webSocketContext,
            ch.getLocation().getRoom().getId(),
            new Output("[yellow]%s begins editing.", ch.getCharacter().getName()),
            ch);

        return getApplicationContext().getBean("nonPlayerCreatureEditorQuestion", Question.class);
    }

    private Optional<MudCharacterTemplate> findNpcToEdit(MudCharacter ch, String token) {
        try {
            Long id = Long.parseLong(token);

            return getRepositoryBundle().getCharacterPrototypeRepository().findById(id);
        } catch (NumberFormatException e) {
            Optional<MudCharacter> npc = findRoomCharacter(ch, token);

            if (npc.isPresent()) {
                return npc.flatMap(npcTemplate -> getRepositoryBundle().getCharacterPrototypeRepository().findById(npc.get().getTemplate().getId()));
            }
        }

        return Optional.empty();
    }
}
