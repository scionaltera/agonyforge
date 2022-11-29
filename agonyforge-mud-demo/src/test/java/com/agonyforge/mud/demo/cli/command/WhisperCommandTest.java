package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import com.agonyforge.mud.models.dynamodb.service.CommService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.TYPE_PC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WhisperCommandTest {
    @Mock
    private CommService commService;

    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private MudCharacter ch;

    @Mock
    private MudCharacter target;

    @Mock
    private MudCharacter other;

    @Mock
    private WebSocketContext webSocketContext;

    @Mock
    private Question question;

    @Captor
    private ArgumentCaptor<Output> outputCaptor;

    @ParameterizedTest
    @ValueSource(strings = {
        "whisper t test",
        "whisper t  test",
        "whisper t   test",
        "whisper t test ",
        "whisper t test test",
        "whisper t test test test"
    })
    void testExecute(String val) {
        String match = val.substring(9).stripLeading();
        List<String> tokens = tokenize(val);
        UUID chId = UUID.randomUUID();
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.of(ch));
        when(characterRepository.getByRoom(eq(100L))).thenReturn(List.of(ch, target, other));

        when(ch.getName()).thenReturn("Scion");
        when(ch.getRoomId()).thenReturn(100L);
        when(target.getName()).thenReturn("Target");

        Input input = new Input(val);
        Output output = new Output();
        WhisperCommand uut = new WhisperCommand(characterRepository, commService);
        Question response = uut.execute(question, webSocketContext, tokens, input, output);

        assertEquals(question, response);
        assertEquals(1, output.getOutput().size());
        assertEquals("[red]You whisper to Target, '" + match + "[red]'", output.getOutput().get(0));

        verify(characterRepository).getById(eq(chId), anyBoolean());
        verify(commService).sendTo(eq(target), outputCaptor.capture());
        verify(commService).sendToRoom(eq(webSocketContext), eq(100L), outputCaptor.capture(), eq(target));
        verifyNoMoreInteractions(commService);

        List<Output> captured = outputCaptor.getAllValues();

        Output toTarget = captured.get(0);
        assertTrue(toTarget.getOutput()
            .stream()
            .anyMatch(line -> line.equals("[red]Scion whispers to you, '" + match + "[red]'")));

        Output toOther = captured.get(1);
        assertTrue(toOther.getOutput()
            .stream()
            .anyMatch(line -> line.equals("[red]Scion whispers something to Target.")));
    }

    @Test
    void testExecuteNoCharacter() {
        UUID chId = UUID.randomUUID();
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));

        Input input = new Input("whisper t test");
        Output output = new Output();
        WhisperCommand uut = new WhisperCommand(characterRepository, commService);
        Question response = uut.execute(question, webSocketContext, List.of("WHISPER", "T", "TEST"), input, output);

        assertEquals(question, response);

        verify(commService, never()).sendTo(any(MudCharacter.class), any(Output.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "whisper",
        "whisper ",
        "whisper  "
    })
    void testExecuteNoTarget(String val) {
        List<String> tokens = tokenize(val);
        Input input = new Input(val);
        Output output = new Output();

        WhisperCommand uut = new WhisperCommand(characterRepository, commService);
        Question response = uut.execute(question, webSocketContext, tokens, input, output);

        assertEquals(question, response);
        assertEquals(1, output.getOutput().size());
        assertEquals("[default]Who would you like to whisper to?", output.getOutput().get(0));

        verify(commService, never()).sendTo(any(MudCharacter.class), any(Output.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "whisper t",
        "whisper t ",
        "whisper t  "
    })
    void testExecuteNoMessage(String val) {
        List<String> tokens = tokenize(val);
        Input input = new Input(val);
        Output output = new Output();

        WhisperCommand uut = new WhisperCommand(characterRepository, commService);
        Question response = uut.execute(question, webSocketContext, tokens, input, output);

        assertEquals(question, response);
        assertEquals(1, output.getOutput().size());
        assertEquals("[default]What would you like to whisper to them?", output.getOutput().get(0));

        verify(commService, never()).sendTo(any(MudCharacter.class), any(Output.class));
    }

    @Test
    void testExecuteTargetNotFound() {
        List<String> tokens = tokenize("whisper t foo");
        Input input = new Input("whisper t foo");
        Output output = new Output();
        UUID chId = UUID.randomUUID();

        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.of(ch));

        WhisperCommand uut = new WhisperCommand(characterRepository, commService);
        Question response = uut.execute(question, webSocketContext, tokens, input, output);

        assertEquals(question, response);
        assertEquals(1, output.getOutput().size());
        assertEquals("[default]There isn't anyone by that name.", output.getOutput().get(0));

        verify(commService, never()).sendTo(any(MudCharacter.class), any(Output.class));
    }

    @Test
    void testExecuteTargetIsSelf() {
        List<String> tokens = tokenize("whisper s foo");
        Input input = new Input("whisper s foo");
        Output output = new Output();
        UUID chId = UUID.randomUUID();

        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.of(ch));
        when(characterRepository.getByRoom(eq(100L))).thenReturn(List.of(ch));
        when(ch.getName()).thenReturn("Scion");
        when(ch.getRoomId()).thenReturn(100L);

        WhisperCommand uut = new WhisperCommand(characterRepository, commService);
        Question response = uut.execute(question, webSocketContext, tokens, input, output);

        assertEquals(question, response);
        assertEquals(1, output.getOutput().size());
        assertEquals("[default]You whisper quietly to yourself.", output.getOutput().get(0));

        verify(commService, never()).sendTo(any(MudCharacter.class), any(Output.class));
    }

    private List<String> tokenize(String val) {
        return Arrays
            .stream(val.split(" "))
            .map(String::toUpperCase)
            .collect(Collectors.toList());
    }
}
