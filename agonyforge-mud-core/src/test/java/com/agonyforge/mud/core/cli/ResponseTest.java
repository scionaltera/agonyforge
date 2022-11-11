package com.agonyforge.mud.core.cli;

import com.agonyforge.mud.core.web.model.Output;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ResponseTest {
    @Mock
    private Question question;

    @Test
    void testResponseSingleArgConstructor() {
        Response uut = new Response(question);

        assertEquals(question, uut.getNext());
        assertEquals(Optional.empty(), uut.getFeedback());
    }

    @Test
    void testResponseTwoArgConstructor() {
        Output output = new Output("feedback");
        Response uut = new Response(question, output);

        assertEquals(question, uut.getNext());
        assertEquals(output, uut.getFeedback().orElseThrow());
    }
}
