package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.service.EchoService;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
public class EchoQuestionTest {
    @Mock
    private EchoService echoService;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private MudCharacter ch;

    @Mock
    private WebSocketContext wsContext;

    @Test
    void testPrompt() {
        UUID chId = UUID.randomUUID();
        when(wsContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));

        when(characterRepository.getById(eq(chId), eq(true))).thenReturn(Optional.of(ch));

        EchoQuestion uut = new EchoQuestion(echoService, applicationContext, characterRepository);
        Output output = uut.prompt(wsContext);

        assertEquals(2, output.getOutput().size());
        assertEquals("", output.getOutput().get(0));
        assertEquals("[green]null[default]> ", output.getOutput().get(1));

        verify(echoService, never()).echoToAll(any(), any());
    }

    @Test
    void testPromptNoCharacter() {
        EchoQuestion uut = new EchoQuestion(echoService, applicationContext, characterRepository);
        Output output = uut.prompt(wsContext);

        assertEquals(3, output.getOutput().size());
        assertEquals("[red]Unable to find your character! The error has been reported.", output.getOutput().get(0));
        assertEquals("", output.getOutput().get(1));
        assertEquals("[default]> ", output.getOutput().get(2));

        verify(echoService, never()).echoToAll(any(), any());
    }

    @Test
    void testAnswerBlank() {
        Input input = new Input("");
        EchoQuestion uut = new EchoQuestion(echoService, applicationContext, characterRepository);
        Response response = uut.answer(wsContext, input);
        Output responseOut = response.getFeedback().orElseThrow();

        assertEquals(1, responseOut.getOutput().size());
        assertEquals("[default]What would you like to say?", responseOut.getOutput().get(0));

        verify(echoService, never()).echoToAll(any(), any());
    }

    @Test
    void testAnswerNoCharacter() {
        Input input = new Input("test");
        EchoQuestion uut = new EchoQuestion(echoService, applicationContext, characterRepository);
        Response response = uut.answer(wsContext, input);
        Output responseOut = response.getFeedback().orElseThrow();

        assertEquals(1, responseOut.getOutput().size());
        assertEquals("[red]Unable to find your character! The error has been reported.", responseOut.getOutput().get(0));

        verify(echoService, never()).echoToAll(any(), any());
    }

    @Test
    void testAnswer() {
        UUID chId = UUID.randomUUID();
        when(wsContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(characterRepository.getById(eq(chId), eq(true))).thenReturn(Optional.of(ch));

        Input input = new Input("test");
        EchoQuestion uut = new EchoQuestion(echoService, applicationContext, characterRepository);
        Response response = uut.answer(wsContext, input);
        Output responseOut = response.getFeedback().orElseThrow();

        assertEquals(1, responseOut.getOutput().size());
        assertEquals("[cyan]You say, 'test[cyan]'", responseOut.getOutput().get(0));

        verify(echoService).echoToAll(eq(wsContext), any(Output.class));
    }
}
