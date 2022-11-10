package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.service.EchoService;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EchoQuestionTest {
    @Mock
    private Principal principal;

    @Mock
    private EchoService echoService;

    @Mock
    private FindByIndexNameSessionRepository<Session> sessionRepository;

    @Mock
    private Session session;

    @Test
    void testPrompt() {
        EchoQuestion uut = new EchoQuestion(echoService, sessionRepository);
        Output output = uut.prompt(principal, session);

        assertEquals("", output.getOutput().get(0));
        assertEquals("[green]null[default]> ", output.getOutput().get(1));

        verify(echoService, never()).echoToAll(any(), any());
    }

    @Test
    void testAnswerBlank() {
        Input input = new Input("");
        EchoQuestion uut = new EchoQuestion(echoService, sessionRepository);
        Response response = uut.answer(principal, session, input);
        Output responseOut = response.getFeedback().orElseThrow();

        assertEquals(1, responseOut.getOutput().size());
        assertEquals("[default]What would you like to say?", responseOut.getOutput().get(0));

        verify(echoService, never()).echoToAll(any(), any());
    }

    @Test
    void testAnswer() {
        Input input = new Input("test");
        EchoQuestion uut = new EchoQuestion(echoService, sessionRepository);
        Response response = uut.answer(principal, session, input);
        Output responseOut = response.getFeedback().orElseThrow();

        assertEquals(1, responseOut.getOutput().size());
        assertEquals("[cyan]You say, 'test[cyan]'", responseOut.getOutput().get(0));

        verify(echoService).echoToAll(eq(principal), any(Output.class));
    }
}
