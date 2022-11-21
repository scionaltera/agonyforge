package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CharacterNameQuestionTest {
    @Mock
    private Principal principal;

    @Mock
    private Question question;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private WebSocketContext webSocketContext;

    @Captor
    private ArgumentCaptor<MudCharacter> characterCaptor;

    @Test
    void testPrompt() {
        CharacterNameQuestion uut = new CharacterNameQuestion(applicationContext, characterRepository, question);
        Output result = uut.prompt(webSocketContext);

        assertEquals(1, result.getOutput().size());
        assertEquals("[default]By what name do you wish to be known? ", result.getOutput().get(0));

        verifyNoInteractions(webSocketContext);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Scion",
        "Sc",
        "Scionscionsc",
        "SCION"
    })
    void testAnswer(String userInput) {
        Map<String, Object> attributes = new HashMap<>();

        when(principal.getName()).thenReturn("principal");
        when(webSocketContext.getPrincipal()).thenReturn(principal);
        when(webSocketContext.getAttributes()).thenReturn(attributes);

        CharacterNameQuestion uut = new CharacterNameQuestion(applicationContext, characterRepository, question);
        Input input = new Input(userInput);
        Response result = uut.answer(webSocketContext, input);
        Output output = result.getFeedback().orElseThrow();

        assertEquals(question, result.getNext());

        assertEquals(1, output.getOutput().size());
        assertEquals(String.format("[default]Hello, [white]%s[default]!", userInput), output.getOutput().get(0));

        verify(characterRepository).save(characterCaptor.capture());

        MudCharacter ch = characterCaptor.getValue();

        assertNotNull(ch.getId());
        assertEquals(principal.getName(), ch.getUser());
        assertEquals(userInput, ch.getName());

        assertEquals(ch.getId(), attributes.get(MUD_CHARACTER));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "S"
    })
    void testAnswerTooShort(String userInput) {
        CharacterNameQuestion uut = new CharacterNameQuestion(applicationContext, characterRepository, question);
        Input input = new Input(userInput);
        Response result = uut.answer(webSocketContext, input);
        Output output = result.getFeedback().orElseThrow();

        assertEquals(uut, result.getNext());

        assertEquals(1, output.getOutput().size());
        assertEquals("[red]Names need to be at least two letters in length.", output.getOutput().get(0));

        verify(webSocketContext, never()).getAttributes();
    }

    @Test
    void testAnswerTooLong() {
        CharacterNameQuestion uut = new CharacterNameQuestion(applicationContext, characterRepository, question);
        Input input = new Input("S".repeat(13));
        Response result = uut.answer(webSocketContext, input);
        Output output = result.getFeedback().orElseThrow();

        assertEquals(uut, result.getNext());

        assertEquals(1, output.getOutput().size());
        assertEquals("[red]Names need to be 12 or fewer letters in length.", output.getOutput().get(0));

        verify(webSocketContext, never()).getAttributes();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "11111",
        "Sc1on"
    })
    void testAnswerInvalidLetters(String userInput) {
        CharacterNameQuestion uut = new CharacterNameQuestion(applicationContext, characterRepository, question);
        Response result = uut.answer(webSocketContext, new Input(userInput));
        Output output = result.getFeedback().orElseThrow();

        assertEquals(uut, result.getNext());

        assertEquals(1, output.getOutput().size());
        assertEquals("[red]Names may only have letters in them.", output.getOutput().get(0));

        verify(webSocketContext, never()).getAttributes();
    }

    @Test
    void testAnswerNoCaps() {
        String userInput = "scion";
        CharacterNameQuestion uut = new CharacterNameQuestion(applicationContext, characterRepository, question);
        Response result = uut.answer(webSocketContext, new Input(userInput));
        Output output = result.getFeedback().orElseThrow();

        assertEquals(uut, result.getNext());

        assertEquals(1, output.getOutput().size());
        assertEquals("[red]Names must begin with a capital letter.", output.getOutput().get(0));

        verify(webSocketContext, never()).getAttributes();
    }
}
