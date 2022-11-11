package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.session.Session;

import java.security.Principal;

import static com.agonyforge.mud.demo.cli.NameQuestion.NAME_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class NameQuestionTest {
    @Mock
    private Principal principal;

    @Mock
    private Session session;

    @Mock
    private Question question;

    @Test
    void testPrompt() {
        NameQuestion uut = new NameQuestion(question);
        Output result = uut.prompt(principal, session);

        assertEquals(1, result.getOutput().size());
        assertEquals("[default]By what name do you wish to be known? ", result.getOutput().get(0));

        verifyNoInteractions(session);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Scion",
        "Sc",
        "Scionscionsc",
        "SCION"
    })
    void testAnswer(String userInput) {
        NameQuestion uut = new NameQuestion(question);
        Input input = new Input(userInput);
        Response result = uut.answer(principal, session, input);
        Output output = result.getFeedback().orElseThrow();

        assertEquals(question, result.getNext());

        assertEquals(1, output.getOutput().size());
        assertEquals(String.format("[default]Hello, [white]%s[default]!", userInput), output.getOutput().get(0));

        verify(session).setAttribute(eq(NAME_KEY), eq(input.getInput()));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "S"
    })
    void testAnswerTooShort(String userInput) {
        NameQuestion uut = new NameQuestion(question);
        Input input = new Input(userInput);
        Response result = uut.answer(principal, session, input);
        Output output = result.getFeedback().orElseThrow();

        assertEquals(uut, result.getNext());

        assertEquals(1, output.getOutput().size());
        assertEquals("[red]Names need to be at least two letters in length.", output.getOutput().get(0));

        verifyNoInteractions(session);
    }

    @Test
    void testAnswerTooLong() {
        NameQuestion uut = new NameQuestion(question);
        Input input = new Input("S".repeat(13));
        Response result = uut.answer(principal, session, input);
        Output output = result.getFeedback().orElseThrow();

        assertEquals(uut, result.getNext());

        assertEquals(1, output.getOutput().size());
        assertEquals("[red]Names need to be 12 or fewer letters in length.", output.getOutput().get(0));

        verifyNoInteractions(session);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "11111",
        "Sc1on"
    })
    void testAnswerInvalidLetters(String userInput) {
        NameQuestion uut = new NameQuestion(question);
        Response result = uut.answer(principal, session, new Input(userInput));
        Output output = result.getFeedback().orElseThrow();

        assertEquals(uut, result.getNext());

        assertEquals(1, output.getOutput().size());
        assertEquals("[red]Names may only have letters in them.", output.getOutput().get(0));

        verifyNoInteractions(session);
    }
}
