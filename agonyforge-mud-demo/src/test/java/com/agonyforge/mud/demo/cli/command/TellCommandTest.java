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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.*;
import java.util.stream.Collectors;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TellCommandTest {
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
    private MudCharacter target;

    @Mock
    private MudCharacter other;

    @Mock
    private CharacterComponent chCharacterComponent, targetCharacterComponent;

    @Mock
    private WebSocketContext webSocketContext;

    @Mock
    private Question question;

    @Captor
    private ArgumentCaptor<Output> outputCaptor;

    private final Random random = new Random();

    @BeforeEach
    void setUp() {
        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        lenient().when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
        lenient().when(repositoryBundle.getRoomRepository()).thenReturn(roomRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "tell t test",
        "tell t  test",
        "tell t   test",
        "tell t test ",
        "tell t test test",
        "tell t test test test",
        "tell t hax %s hax"
    })
    void testExecute(String val) {
        String match = val.substring(7).stripLeading();
        List<String> tokens = tokenize(val);
        Long chId = random.nextLong();

        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(characterRepository.findAll()).thenReturn(List.of(ch, target, other));
        when(ch.getCharacter()).thenReturn(chCharacterComponent);
        when(target.getCharacter()).thenReturn(targetCharacterComponent);
        when(chCharacterComponent.getName()).thenReturn("Scion");
        when(targetCharacterComponent.getName()).thenReturn("Target");

        Input input = new Input(val);
        Output output = new Output();
        TellCommand uut = new TellCommand(repositoryBundle, commService, applicationContext);
        Question response = uut.execute(question, webSocketContext, tokens, input, output);

        assertEquals(question, response);
        assertEquals(1, output.getOutput().size());
        assertEquals("[red]You tell Target, '" + match + "[red]'", output.getOutput().get(0));

        verify(characterRepository).findById(eq(chId));
        verify(commService).sendTo(eq(target), outputCaptor.capture());
        verifyNoMoreInteractions(commService);

        Output toTarget = outputCaptor.getValue();
        assertTrue(toTarget.getOutput()
            .stream()
            .anyMatch(line -> line.equals("[red]Scion tells you, '" + match + "[red]'")));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "tell",
        "tell ",
        "tell  "
    })
    void testExecuteNoTarget(String val) {
        List<String> tokens = tokenize(val);
        Input input = new Input(val);
        Output output = new Output();

        TellCommand uut = new TellCommand(repositoryBundle, commService, applicationContext);
        Question response = uut.execute(question, webSocketContext, tokens, input, output);

        assertEquals(question, response);
        assertEquals(1, output.getOutput().size());
        assertEquals("[default]Who would you like to tell?", output.getOutput().get(0));

        verify(commService, never()).sendTo(any(MudCharacter.class), any(Output.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "tell t",
        "tell t ",
        "tell t  "
    })
    void testExecuteNoMessage(String val) {
        List<String> tokens = tokenize(val);
        Input input = new Input(val);
        Output output = new Output();

        TellCommand uut = new TellCommand(repositoryBundle, commService, applicationContext);
        Question response = uut.execute(question, webSocketContext, tokens, input, output);

        assertEquals(question, response);
        assertEquals(1, output.getOutput().size());
        assertEquals("[default]What would you like to tell them?", output.getOutput().get(0));

        verify(commService, never()).sendTo(any(MudCharacter.class), any(Output.class));
    }

    @Test
    void testExecuteTargetNotFound() {
        List<String> tokens = tokenize("tell t foo");
        Input input = new Input("tell t foo");
        Output output = new Output();
        Long chId = random.nextLong();

        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));

        TellCommand uut = new TellCommand(repositoryBundle, commService, applicationContext);
        Question response = uut.execute(question, webSocketContext, tokens, input, output);

        assertEquals(question, response);
        assertEquals(1, output.getOutput().size());
        assertEquals("[default]There isn't anyone by that name.", output.getOutput().get(0));

        verify(commService, never()).sendTo(any(MudCharacter.class), any(Output.class));
    }

    private List<String> tokenize(String val) {
        return Arrays
            .stream(val.split(" "))
            .map(String::toUpperCase)
            .collect(Collectors.toList());
    }
}
