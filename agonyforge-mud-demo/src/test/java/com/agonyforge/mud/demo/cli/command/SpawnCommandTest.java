package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.cli.TokenType;
import com.agonyforge.mud.demo.model.constant.Pronoun;
import com.agonyforge.mud.demo.model.impl.*;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import com.agonyforge.mud.demo.service.CommService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SpawnCommandTest {
    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private CommService commService;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private Question question;

    @Mock
    private WebSocketContext webSocketContext;

    @Mock
    private Output output;

    @Mock
    private Binding cmdBinding;

    @Mock
    private MudCharacterTemplate npcTemplate;

    @Mock
    private MudCharacter ch, npc;

    @Mock
    private LocationComponent chLocation, npcLocation;

    @Mock
    private CharacterComponent chCharacter, npcCharacter;

    @Mock
    private MudRoom room;

    private final long chId = 75L;
    private final long roomId = 300L;

    @Test
    public void testSpawn() {
        when(room.getId()).thenReturn(roomId);

        when(ch.getLocation()).thenReturn(chLocation);
        when(chLocation.getRoom()).thenReturn(room);
        when(ch.getCharacter()).thenReturn(chCharacter);
        when(chCharacter.getName()).thenReturn("Scion");
        when(chCharacter.getPronoun()).thenReturn(Pronoun.HE);

        when(npcTemplate.buildInstance()).thenReturn(npc);
        when(npc.getLocation()).thenReturn(npcLocation);
        when(npc.getCharacter()).thenReturn(npcCharacter);
        when(npcCharacter.getName()).thenReturn("Noob");

        when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        when(webSocketContext.getAttributes()).thenReturn(Map.of(MUD_CHARACTER, chId));
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(characterRepository.save(eq(npc))).thenReturn(npc);

        SpawnCommand uut = new SpawnCommand(repositoryBundle, commService, applicationContext);
        Binding npcBinding = new Binding(TokenType.NPC_ID, "target", npcTemplate);
        Question result = uut.execute(question, webSocketContext, List.of(cmdBinding, npcBinding), output);

        assertEquals(question, result);

        verify(npcTemplate).buildInstance();
        verify(npcLocation).setRoom(eq(room));
        verify(characterRepository).save(eq(npc));
        verify(commService).sendToRoom(eq(roomId), any(Output.class), eq(ch));
    }
}
