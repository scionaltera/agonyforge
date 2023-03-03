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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShoutCommandTest {
    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private MudItemRepository itemRepository;

    @Mock
    private MudRoomRepository roomRepository;

    @Mock
    private CommService commService;

    @Mock
    private MudCharacter ch;

    @Mock
    private WebSocketContext webSocketContext;

    @Mock
    private Question question;

    @ParameterizedTest
    @ValueSource(strings = {
        "shout test",
        "shout  test",
        "shout   test",
        "shout test ",
        "shout test test",
        "shout test test test",
        "shout hax %s hax"
    })
    void testExecute(String val) {
        String match = val.substring(6).stripLeading();
        List<String> tokens = tokenize(val);
        UUID chId = UUID.randomUUID();

        when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
        when(repositoryBundle.getRoomRepository()).thenReturn(roomRepository);
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.of(ch));
        when(ch.getZoneId()).thenReturn(1L);

        Input input = new Input(val);
        Output output = new Output();
        ShoutCommand uut = new ShoutCommand(repositoryBundle, commService);
        Question response = uut.execute(question, webSocketContext, tokens, input, output);

        assertEquals(question, response);
        assertEquals(1, output.getOutput().size());
        assertEquals("[dyellow]You shout, '" + match + "[dyellow]'", output.getOutput().get(0));

        verify(characterRepository).getById(eq(chId), anyBoolean());
        verify(commService).sendToZone(eq(webSocketContext), eq(1L), any(Output.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "shout",
        "shout ",
        "shout  ",
        "shout\t"
    })
    void testExecuteNoMessage(String val) {
        when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
        when(repositoryBundle.getRoomRepository()).thenReturn(roomRepository);

        List<String> tokens = tokenize(val);
        Input input = new Input(val);
        Output output = new Output();
        ShoutCommand uut = new ShoutCommand(repositoryBundle, commService);
        Question response = uut.execute(question, webSocketContext, tokens, input, output);

        assertEquals(question, response);
        assertEquals(1, output.getOutput().size());
        assertEquals("[default]What would you like to shout?", output.getOutput().get(0));

        verify(characterRepository, never()).getById(any(UUID.class), anyBoolean());
        verify(commService, never()).sendToZone(any(WebSocketContext.class), anyLong(), any(Output.class));
    }

    private List<String> tokenize(String val) {
        return Arrays
            .stream(val.split(" "))
            .map(String::toUpperCase)
            .collect(Collectors.toList());
    }
}
