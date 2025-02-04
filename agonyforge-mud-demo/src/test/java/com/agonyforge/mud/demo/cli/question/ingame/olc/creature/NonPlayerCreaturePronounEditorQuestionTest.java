package com.agonyforge.mud.demo.cli.question.ingame.olc.creature;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.constant.Pronoun;
import com.agonyforge.mud.demo.model.impl.*;
import com.agonyforge.mud.demo.model.repository.MudCharacterPrototypeRepository;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static com.agonyforge.mud.demo.cli.question.ingame.olc.creature.NonPlayerCreatureEditorQuestion.MEDIT_MODEL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NonPlayerCreaturePronounEditorQuestionTest {
    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private MudCharacterRepository mudCharacterRepository;

    @Mock
    private MudCharacterPrototypeRepository mudCharacterPrototypeRepository;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private WebSocketContext webSocketContext;

    @Mock
    private MudRoom room;

    @Mock
    private MudCharacter ch;

    @Mock
    private MudCharacterTemplate chTemplate;

    @Mock
    private CharacterComponent chComponent, chTempComponent;

    @Mock
    private LocationComponent chLocation;

    @Mock
    private Question question, mobEditorQuestion;

    @BeforeEach
    void setUp() {
        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(mudCharacterRepository);
        lenient().when(repositoryBundle.getCharacterPrototypeRepository()).thenReturn(mudCharacterPrototypeRepository);

        lenient().when(applicationContext.getBean(eq("nonPlayerCreaturePronounEditorQuestion"), eq(Question.class))).thenReturn(question);
        lenient().when(applicationContext.getBean(eq("nonPlayerCreatureEditorQuestion"), eq(Question.class))).thenReturn(mobEditorQuestion);

        lenient().when(chTemplate.getCharacter()).thenReturn(chTempComponent);

        lenient().when(ch.getCharacter()).thenReturn(chComponent);
        lenient().when(ch.getLocation()).thenReturn(chLocation);
        lenient().when(ch.getCharacter().getName()).thenReturn("Scion");
        lenient().when(ch.getLocation().getRoom()).thenReturn(room);
    }

    @Test
    void testPrompt() {
        when(webSocketContext.getAttributes()).thenReturn(Map.of(MEDIT_MODEL, 65L));
        when(mudCharacterPrototypeRepository.findById(eq(65L))).thenReturn(Optional.of(chTemplate));

        NonPlayerCreaturePronounEditorQuestion uut = new NonPlayerCreaturePronounEditorQuestion(applicationContext, repositoryBundle);
        Output result = uut.prompt(webSocketContext);

        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("Non-Player Character Pronouns")));

        Arrays.stream(Pronoun.values()).forEach(pronoun -> {
            assertTrue(result.getOutput().stream().anyMatch(line -> line.contains(pronoun.getObject())));
            assertTrue(result.getOutput().stream().anyMatch(line -> line.contains(pronoun.getSubject())));
        });
    }

    @Test
    void testAnswerExit() {
        when(webSocketContext.getAttributes()).thenReturn(Map.of(MEDIT_MODEL, 65L));
        when(mudCharacterPrototypeRepository.findById(eq(65L))).thenReturn(Optional.of(chTemplate));

        NonPlayerCreaturePronounEditorQuestion uut = new NonPlayerCreaturePronounEditorQuestion(applicationContext, repositoryBundle);
        Response result = uut.answer(webSocketContext, new Input("x"));

        assertEquals(mobEditorQuestion, result.getNext());
    }

    @ParameterizedTest
    @ValueSource(strings = { "1", "2", "3", "4" })
    void testAnswerSelectPronoun(String input) {
        when(webSocketContext.getAttributes()).thenReturn(Map.of(MEDIT_MODEL, 65L));
        when(mudCharacterPrototypeRepository.findById(eq(65L))).thenReturn(Optional.of(chTemplate));

        NonPlayerCreaturePronounEditorQuestion uut = new NonPlayerCreaturePronounEditorQuestion(applicationContext, repositoryBundle);
        Response result = uut.answer(webSocketContext, new Input(input));

        assertEquals(mobEditorQuestion, result.getNext());

        verify(chTempComponent).setPronoun(any(Pronoun.class));
        verify(mudCharacterPrototypeRepository).save(chTemplate);
    }

    @ParameterizedTest
    @ValueSource(strings = { "-1", "0", "5", "what" })
    void testAnswerSelectPronounInvalid(String input) {
        when(webSocketContext.getAttributes()).thenReturn(Map.of(MEDIT_MODEL, 65L));
        when(mudCharacterPrototypeRepository.findById(eq(65L))).thenReturn(Optional.of(chTemplate));

        NonPlayerCreaturePronounEditorQuestion uut = new NonPlayerCreaturePronounEditorQuestion(applicationContext, repositoryBundle);
        Response result = uut.answer(webSocketContext, new Input(input));

        assertEquals(question, result.getNext());
        assertTrue(result.getFeedback().orElseThrow().getOutput().stream().anyMatch(line -> line.contains("Please choose one of the menu items.")));

        verify(chTempComponent, never()).setPronoun(any(Pronoun.class));
        verify(mudCharacterPrototypeRepository, never()).save(chTemplate);
    }
}
