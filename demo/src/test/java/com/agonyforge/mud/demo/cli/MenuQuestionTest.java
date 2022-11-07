package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MenuQuestionTest {
    @Mock
    private Principal principal;

    @Mock
    private Question question;

    @Test
    void testPrompt() {
        MenuQuestion uut = new MenuQuestion();
        uut.setNextQuestion(question);

        Output result = uut.prompt(principal);

        assertEquals(9, result.getOutput().size());
        assertEquals("[dcyan]*************", result.getOutput().get(0));
        assertEquals("[dcyan]* [cyan]    Demo Menu    [dcyan] *", result.getOutput().get(1));
        assertEquals("[dcyan]*************", result.getOutput().get(2));
        assertEquals("[cyan]F[dcyan]) [cyan]Foo", result.getOutput().get(3));
        assertEquals("[cyan]B[dcyan]) [cyan]Bar", result.getOutput().get(4));
        assertEquals("[cyan]C[dcyan]) [cyan]Crazy Town", result.getOutput().get(5));
        assertEquals("[cyan]Z[dcyan]) [cyan]Zed's Dead, Baby", result.getOutput().get(6));
        assertEquals("[cyan]P[dcyan]) [cyan]Puerto Rico", result.getOutput().get(7));
        assertEquals("[cyan]Please [dcyan]make your selection[cyan]: ", result.getOutput().get(8));

    }

    @ParameterizedTest
    @CsvSource({
        "F,Bar!",
        "B,Baz!",
        "C,I'm the only sane one around here.",
        "Z,Royale with cheese.",
        "P,Rico Suave!"
    })
    void testAnswers(String letter, String expected) {
        MenuQuestion uut = new MenuQuestion();
        uut.setNextQuestion(question);

        Input input = new Input(letter);
        Response result = uut.answer(principal, input);

        assertEquals(question, result.getNext());

        List<String> lines = result.getFeedback().orElseThrow().getOutput();
        assertEquals(1, lines.size());
        assertEquals(expected, lines.get(0));
    }

    @Test
    void testAnswerInvalid() {
        MenuQuestion uut = new MenuQuestion();
        uut.setNextQuestion(question);

        Input input = new Input("A");
        Response result = uut.answer(principal, input);

        assertEquals(uut, result.getNext());

        List<String> lines = result.getFeedback().orElseThrow().getOutput();
        assertEquals(1, lines.size());
        assertEquals("Please choose one of the menu options.", lines.get(0));
    }
}
