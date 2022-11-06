package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class EchoQuestionTest {
    @Mock
    private Principal principal;

    @Test
    void testPrompt() {
        EchoQuestion uut = new EchoQuestion();
        Output output = uut.prompt(principal);

        assertEquals("", output.getOutput().get(0));
        assertEquals("[default]> ", output.getOutput().get(1));
    }

    @Test
    void testAnswerBlank() {
        Input input = new Input("");
        EchoQuestion uut = new EchoQuestion();
        Response response = uut.answer(principal, input);
        Output responseOut = response.getFeedback().orElseThrow();

        assertEquals(1, responseOut.getOutput().size());
        assertEquals("[default]What would you like to say?", responseOut.getOutput().get(0));
    }

    @Test
    void testAnswer() {
        Input input = new Input("test");
        EchoQuestion uut = new EchoQuestion();
        Response response = uut.answer(principal, input);
        Output responseOut = response.getFeedback().orElseThrow();

        assertEquals(1, responseOut.getOutput().size());
        assertEquals("[cyan]You say, 'test[cyan]'", responseOut.getOutput().get(0));
    }
}
