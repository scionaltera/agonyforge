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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SlayCommandTest {
    private static final Random RANDOM = new Random();

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
    private CommandReference commandRef;

    @Mock
    private MudCharacter ch, target;

    @Mock
    private LocationComponent chLocation, targetLocation;

    @Mock
    private CharacterComponent chComponent, targetComponent;

    @Mock
    private MudRoom room;

    private final long chId = RANDOM.nextLong();
    private final long roomId = RANDOM.nextLong();

    @BeforeEach
    void setUp() {
        when(ch.getLocation()).thenReturn(chLocation);
        when(ch.getCharacter()).thenReturn(chComponent);
        when(chLocation.getRoom()).thenReturn(room);
        when(chComponent.getName()).thenReturn("Scion");
        when(chComponent.getPronoun()).thenReturn(Pronoun.HE);

        when(target.getCharacter()).thenReturn(targetComponent);
        when(targetComponent.getName()).thenReturn("Target");

        when(room.getId()).thenReturn(roomId);

        when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        when(webSocketContext.getAttributes()).thenReturn(Map.of(MUD_CHARACTER, chId));
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
    }

    @Test
    public void testSlay() {
        SlayCommand uut = new SlayCommand(repositoryBundle, commService, applicationContext);
        Binding command = new Binding(TokenType.COMMAND, "slay", commandRef);
        Binding binding = new Binding(TokenType.NPC_IN_ROOM, "target", target);
        Question result = uut.execute(question, webSocketContext, List.of(command, binding), output);

        assertEquals(question, result);

        verify(characterRepository).delete(eq(target));
        verify(characterRepository, never()).delete(eq(ch));

        verify(commService).sendToRoom(eq(roomId), any(Output.class), eq(ch));
    }
}
