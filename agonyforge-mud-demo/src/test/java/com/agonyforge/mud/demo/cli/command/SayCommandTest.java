package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.service.EchoService;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SayCommandTest {
    @Mock
    private EchoService echoService;

    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private MudCharacter ch;

    @Mock
    private WebSocketContext webSocketContext;

    @Mock
    private Question question;

    @ParameterizedTest
    @ValueSource(strings = {
        "say test",
        "say  test",
        "say   test",
        "say test ",
        "say test test",
        "say test test test"
    })
    void testExecute(String val) {
        String match = val.substring(4).stripLeading();
        UUID chId = UUID.randomUUID();
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(characterRepository.getById(eq(chId), eq(true))).thenReturn(Optional.of(ch));

        Input input = new Input(val);
        Output output = new Output();
        SayCommand uut = new SayCommand(characterRepository, echoService);
        Question response = uut.execute(question, webSocketContext, input, output);

        assertEquals(question, response);
        assertEquals(1, output.getOutput().size());
        assertEquals("[cyan]You say, '" + match + "[cyan]'", output.getOutput().get(0));

        verify(characterRepository).getById(eq(chId), anyBoolean());
        verify(echoService).echoToAll(eq(webSocketContext), any(Output.class));
    }

    @Test
    void testExecuteNoCharacter() {
        UUID chId = UUID.randomUUID();
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));

        Input input = new Input("say test");
        Output output = new Output();
        SayCommand uut = new SayCommand(characterRepository, echoService);
        Question response = uut.execute(question, webSocketContext, input, output);

        assertEquals(question, response);

        verify(echoService, never()).echoToAll(eq(webSocketContext), any(Output.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "say",
        "say ",
        "say  ",
        "say\t"
    })
    void testExecuteNoMessage(String val) {
        Input input = new Input(val);
        Output output = new Output();
        SayCommand uut = new SayCommand(characterRepository, echoService);
        Question response = uut.execute(question, webSocketContext, input, output);

        assertEquals(question, response);
        assertEquals(1, output.getOutput().size());
        assertEquals("[default]What would you like to say?", output.getOutput().get(0));

        verify(characterRepository, never()).getById(any(UUID.class), anyBoolean());
        verify(echoService, never()).echoToAll(any(WebSocketContext.class), any(Output.class));
    }
}
