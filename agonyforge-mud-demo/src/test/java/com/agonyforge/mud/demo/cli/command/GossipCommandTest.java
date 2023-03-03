package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudItemRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudRoomRepository;
import com.agonyforge.mud.models.dynamodb.service.CommService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GossipCommandTest {
    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private CommService commService;

    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private MudItemRepository itemRepository;

    @Mock
    private MudRoomRepository roomRepository;

    @Mock
    private MudCharacter ch;

    @Mock
    private WebSocketContext webSocketContext;

    @Mock
    private Question question;

    @BeforeEach
    void setUp() {
        when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
        when(repositoryBundle.getRoomRepository()).thenReturn(roomRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "gossip test",
        "gossip  test",
        "gossip   test",
        "gossip test ",
        "gossip test test",
        "gossip test test test",
        "gossip hax %s hax"
    })
    void testExecute(String val) {
        String match = val.substring(7).stripLeading();
        List<String> tokens = tokenize(val);
        UUID chId = UUID.randomUUID();

        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.of(ch));

        Input input = new Input(val);
        Output output = new Output();
        GossipCommand uut = new GossipCommand(repositoryBundle, commService);
        Question response = uut.execute(question, webSocketContext, tokens, input, output);

        assertEquals(question, response);
        assertEquals(1, output.getOutput().size());
        assertEquals("[green]You gossip, '" + match + "[green]'", output.getOutput().get(0));

        verify(characterRepository).getById(eq(chId), anyBoolean());
        verify(commService).sendToAll(eq(webSocketContext), any(Output.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "gossip",
        "gossip ",
        "gossip  ",
        "gossip\t"
    })
    void testExecuteNoMessage(String val) {
        List<String> tokens = tokenize(val);
        Input input = new Input(val);
        Output output = new Output();

        GossipCommand uut = new GossipCommand(repositoryBundle, commService);
        Question response = uut.execute(question, webSocketContext, tokens, input, output);

        assertEquals(question, response);
        assertEquals(1, output.getOutput().size());
        assertEquals("[default]What would you like to gossip?", output.getOutput().get(0));

        verify(characterRepository, never()).getById(any(UUID.class), anyBoolean());
        verify(commService, never()).sendToAll(any(WebSocketContext.class), any(Output.class));
    }

    private List<String> tokenize(String val) {
        return Arrays
            .stream(val.split(" "))
            .map(String::toUpperCase)
            .collect(Collectors.toList());
    }
}
