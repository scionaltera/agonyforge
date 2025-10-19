package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.constant.Effort;
import com.agonyforge.mud.demo.model.constant.Pronoun;
import com.agonyforge.mud.demo.model.constant.Stat;
import com.agonyforge.mud.demo.model.impl.CharacterComponent;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudCharacterTemplate;
import com.agonyforge.mud.demo.model.impl.NonPlayerComponent;
import com.agonyforge.mud.demo.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static com.agonyforge.mud.demo.cli.TokenType.*;
import static com.agonyforge.mud.demo.cli.question.ingame.olc.creature.NonPlayerCreatureEditorQuestion.MEDIT_MODEL;

@Component
public class NonPlayerCreatureEditorCommand extends AbstractCommand {
    @Autowired
    public NonPlayerCreatureEditorCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);

        addSyntax(NPC_IN_ROOM); // edit NPC in room
        addSyntax(NPC_ID);      // edit NPC by ID
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<Binding> bindings, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);
        MudCharacterTemplate npcTemplate;

        if (NPC_ID.equals(bindings.get(1).getType())) {
            Long id = bindings.get(1).asNumber();
            npcTemplate = getRepositoryBundle().getCharacterPrototypeRepository().findById(id).orElseGet(() -> {
                MudCharacterTemplate template = new MudCharacterTemplate();

                template.setId(id);
                template.setComplete(true);

                template.setCharacter(new CharacterComponent());
                template.getCharacter().setName("an unfinished npc");
                template.getCharacter().setPronoun(Pronoun.THEY);
                template.getCharacter().setSpecies(getRepositoryBundle().getSpeciesRepository().findById(1L).orElseThrow());
                template.getCharacter().setProfession(getRepositoryBundle().getProfessionRepository().findById(1L).orElseThrow());

                Arrays.stream(Stat.values()).forEach(stat -> template.getCharacter().setBaseStat(stat, 0));
                Arrays.stream(Effort.values()).forEach(effort -> template.getCharacter().setBaseEffort(effort, 0));

                template.setNonPlayer(new NonPlayerComponent());

                return getRepositoryBundle().getCharacterPrototypeRepository().save(template);
            });
        } else if (NPC_IN_ROOM.equals(bindings.get(1).getType())) {
            MudCharacter npc = bindings.get(1).asCharacter();
            npcTemplate = npc.getTemplate();
        } else {
            output.append("[red]Invalid binding type.");
            return question;
        }

        webSocketContext.getAttributes().put(MEDIT_MODEL, npcTemplate.getId());
        getCommService().sendToRoom(
            ch.getLocation().getRoom().getId(),
            new Output("[yellow]%s begins editing.", ch.getCharacter().getName()),
            ch);

        return getApplicationContext().getBean("nonPlayerCreatureEditorQuestion", Question.class);
    }
}
