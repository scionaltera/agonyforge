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

    @Test
    void testExecute() {
        UUID chId = UUID.randomUUID();
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(characterRepository.getById(eq(chId), eq(true))).thenReturn(Optional.of(ch));

        Input input = new Input("say test");
        Output output = new Output();
        SayCommand uut = new SayCommand(characterRepository, echoService);
        Question response = uut.execute(question, webSocketContext, input, output);

        assertEquals(question, response);

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

    @Test
    void testExecuteNoMessage() {
        Input input = new Input("say");
        Output output = new Output();
        SayCommand uut = new SayCommand(characterRepository, echoService);
        Question response = uut.execute(question, webSocketContext, input, output);

        assertEquals(question, response);

        verify(echoService, never()).echoToAll(any(WebSocketContext.class), any(Output.class));
    }
}
