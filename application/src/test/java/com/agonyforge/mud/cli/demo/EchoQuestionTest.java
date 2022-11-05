package com.agonyforge.mud.cli.demo;

import com.agonyforge.mud.cli.Response;
import com.agonyforge.mud.web.model.Input;
import com.agonyforge.mud.web.model.Output;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class EchoQuestionTest {
    @Test
    void testPrompt() {
        EchoQuestion uut = new EchoQuestion();
        Output output = uut.prompt();

        assertEquals("", output.getOutput().get(0));
        assertEquals("[default]> ", output.getOutput().get(1));
    }

    @Test
    void testAnswerBlank() {
        Input input = new Input("");
        EchoQuestion uut = new EchoQuestion();
        Response response = uut.answer(input);
        Output responseOut = response.getFeedback().get();

        assertEquals(1, responseOut.getOutput().size());
        assertEquals("[default]What would you like to say?", responseOut.getOutput().get(0));
    }

    @Test
    void testAnswer() {
        Input input = new Input("test");
        EchoQuestion uut = new EchoQuestion();
        Response response = uut.answer(input);
        Output responseOut = response.getFeedback().get();

        assertEquals(1, responseOut.getOutput().size());
        assertEquals("[cyan]You say, 'test[cyan]'", responseOut.getOutput().get(0));
    }
}
