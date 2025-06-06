package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.CharacterComponent;
import com.agonyforge.mud.demo.model.impl.LocationComponent;
import com.agonyforge.mud.demo.model.impl.MudRoom;
import com.agonyforge.mud.demo.model.repository.MudItemRepository;
import com.agonyforge.mud.demo.model.repository.MudRoomRepository;
import com.agonyforge.mud.demo.service.CommService;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
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
public class SayCommandTest {
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
    private MudRoom room;

    @Mock
    private CommService commService;

    @Mock
    private MudCharacter ch;

    @Mock
    private CharacterComponent characterComponent;

    @Mock
    private LocationComponent chLocationComponent;

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
        "say test",
        "say  test",
        "say   test",
        "say test ",
        "say test test",
        "say test test test",
        "say hax %s hax"
    })
    void testExecute(String val) {
        String match = val.substring(4).stripLeading();
        List<String> tokens = tokenize(val);
        Long chId = random.nextLong();

        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(room.getId()).thenReturn(100L);
        when(characterComponent.getName()).thenReturn("Name");
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(ch.getCharacter()).thenReturn(characterComponent);
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);

        Input input = new Input(val);
        Output output = new Output();
        SayCommand uut = new SayCommand(repositoryBundle, commService, applicationContext);
        Question response = uut.execute(question, webSocketContext, tokens, input, output);

        assertEquals(question, response);
        assertEquals(1, output.getOutput().size());
        assertEquals("[cyan]You say, '" + match + "[cyan]'", output.getOutput().get(0));

        verify(characterRepository).findById(eq(chId));
        verify(commService).sendToRoom(eq(100L), any(Output.class), eq(ch));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "say",
        "say ",
        "say  ",
        "say\t"
    })
    void testExecuteNoMessage(String val) {
        List<String> tokens = tokenize(val);
        Input input = new Input(val);
        Output output = new Output();
        SayCommand uut = new SayCommand(repositoryBundle, commService, applicationContext);
        Question response = uut.execute(question, webSocketContext, tokens, input, output);

        assertEquals(question, response);
        assertEquals(1, output.getOutput().size());
        assertEquals("[default]What would you like to say?", output.getOutput().get(0));

        verify(characterRepository, never()).findById(any(Long.class));
        verify(commService, never()).sendToRoom(anyLong(), any(Output.class));
    }

    private List<String> tokenize(String val) {
        return Arrays
            .stream(val.split(" "))
            .map(String::toUpperCase)
            .collect(Collectors.toList());
    }
}
