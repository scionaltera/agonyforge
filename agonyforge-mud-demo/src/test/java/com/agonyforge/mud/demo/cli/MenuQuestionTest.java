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
import org.springframework.context.ApplicationContext;
import org.springframework.session.Session;

import java.security.Principal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MenuQuestionTest {
    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private Principal principal;

    @Mock
    private Question question;

    @Mock
    private Session session;

    @Test
    void testPrompt() {
        MenuQuestion uut = new MenuQuestion(applicationContext);
        uut.setNextQuestion("testQuestion");

        Output result = uut.prompt(principal, session);

        assertEquals(11, result.getOutput().size());
        assertEquals("", result.getOutput().get(0));
        assertEquals("[dcyan]***************", result.getOutput().get(1));
        assertEquals("[dcyan]* [cyan] Demo Menu [dcyan] *", result.getOutput().get(2));
        assertEquals("[dcyan]***************", result.getOutput().get(3));
        assertEquals("[cyan]F[dcyan]) [cyan]Foo", result.getOutput().get(4));
        assertEquals("[cyan]B[dcyan]) [cyan]Bar", result.getOutput().get(5));
        assertEquals("[cyan]C[dcyan]) [cyan]Crazy Town", result.getOutput().get(6));
        assertEquals("[cyan]Z[dcyan]) [cyan]Zed's Dead, Baby", result.getOutput().get(7));
        assertEquals("[cyan]P[dcyan]) [cyan]Puerto Rico", result.getOutput().get(8));
        assertEquals("", result.getOutput().get(9));
        assertEquals("[dcyan]Please [cyan]make your selection[dcyan]: ", result.getOutput().get(10));

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
        when(applicationContext.getBean(eq("testQuestion"), eq(Question.class))).thenReturn(question);

        MenuQuestion uut = new MenuQuestion(applicationContext);
        uut.setNextQuestion("testQuestion");

        Input input = new Input(letter);
        Response result = uut.answer(principal, session, input);

        assertEquals(question, result.getNext());

        List<String> lines = result.getFeedback().orElseThrow().getOutput();
        assertEquals(1, lines.size());
        assertEquals(expected, lines.get(0));
    }

    @Test
    void testAnswerInvalid() {
        MenuQuestion uut = new MenuQuestion(applicationContext);
        uut.setNextQuestion("testQuestion");

        Input input = new Input("A");
        Response result = uut.answer(principal, session, input);

        assertEquals(uut, result.getNext());

        List<String> lines = result.getFeedback().orElseThrow().getOutput();
        assertEquals(1, lines.size());
        assertEquals("Please choose one of the menu options.", lines.get(0));
    }
}
