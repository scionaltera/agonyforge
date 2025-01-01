package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.CharacterComponent;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import com.agonyforge.mud.demo.model.repository.MudItemRepository;
import com.agonyforge.mud.demo.model.repository.MudRoomRepository;
import com.agonyforge.mud.demo.service.CommService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.*;
import java.util.stream.Collectors;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShoutCommandTest {
    @Mock
    private ApplicationContext applicationContext;

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
    private CharacterComponent characterComponent;

    @Mock
    private WebSocketContext webSocketContext;

    @Mock
    private Question question;

    private final Random random = new Random();

    @BeforeEach
    void setUp() {
        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        lenient().when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
        lenient().when(repositoryBundle.getRoomRepository()).thenReturn(roomRepository);
    }

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
        Long chId = random.nextLong();

        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(characterComponent.getName()).thenReturn("Steve");
        when(ch.getCharacter()).thenReturn(characterComponent);
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(ch.getZoneId()).thenReturn(1L);

        Input input = new Input(val);
        Output output = new Output();
        ShoutCommand uut = new ShoutCommand(repositoryBundle, commService, applicationContext);
        Question response = uut.execute(question, webSocketContext, tokens, input, output);

        assertEquals(question, response);
        assertEquals(1, output.getOutput().size());
        assertEquals("[dyellow]You shout, '" + match + "[dyellow]'", output.getOutput().get(0));

        verify(characterRepository).findById(eq(chId));
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
        List<String> tokens = tokenize(val);
        Input input = new Input(val);
        Output output = new Output();
        ShoutCommand uut = new ShoutCommand(repositoryBundle, commService, applicationContext);
        Question response = uut.execute(question, webSocketContext, tokens, input, output);

        assertEquals(question, response);
        assertEquals(1, output.getOutput().size());
        assertEquals("[default]What would you like to shout?", output.getOutput().get(0));

        verify(characterRepository, never()).findById(any(Long.class));
        verify(commService, never()).sendToZone(any(WebSocketContext.class), anyLong(), any(Output.class));
    }

    private List<String> tokenize(String val) {
        return Arrays
            .stream(val.split(" "))
            .map(String::toUpperCase)
            .collect(Collectors.toList());
    }
}
