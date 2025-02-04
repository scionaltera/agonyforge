package com.agonyforge.mud.demo.cli.question.ingame.olc.creature;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.*;
import com.agonyforge.mud.demo.model.repository.MudCharacterPrototypeRepository;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import com.agonyforge.mud.demo.service.CommService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static com.agonyforge.mud.demo.cli.question.ingame.olc.creature.NonPlayerCreatureEditorQuestion.MEDIT_MODEL;
import static com.agonyforge.mud.demo.cli.question.ingame.olc.creature.NonPlayerCreatureEditorQuestion.MEDIT_STATE;
import static com.agonyforge.mud.demo.cli.question.ingame.olc.creature.NonPlayerCreatureEditorQuestion.MeditState;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NonPlayerCreatureEditorQuestionTest {
    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private CommService commService;

    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private MudCharacterRepository mudCharacterRepository;

    @Mock
    private MudCharacterPrototypeRepository mudCharacterPrototypeRepository;

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
    private Question question, commandQuestion, pronounEditorQuestion;

    @BeforeEach
    void setUp() {
        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(mudCharacterRepository);
        lenient().when(repositoryBundle.getCharacterPrototypeRepository()).thenReturn(mudCharacterPrototypeRepository);

        lenient().when(applicationContext.getBean(eq("nonPlayerCreatureEditorQuestion"), eq(Question.class))).thenReturn(question);
        lenient().when(applicationContext.getBean(eq("nonPlayerCreaturePronounEditorQuestion"), eq(Question.class))).thenReturn(pronounEditorQuestion);
        lenient().when(applicationContext.getBean(eq("commandQuestion"), eq(Question.class))).thenReturn(commandQuestion);

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
        when(chTemplate.getCharacter()).thenReturn(chComponent);

        NonPlayerCreatureEditorQuestion uut = new NonPlayerCreatureEditorQuestion(applicationContext, repositoryBundle, commService);
        Output result = uut.prompt(webSocketContext);

        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("Non-Player Character Editor")));
    }

    @Test
    void testPromptName() {
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MEDIT_MODEL, 65L,
            MEDIT_STATE, MeditState.NAME
        ));
        when(mudCharacterPrototypeRepository.findById(eq(65L))).thenReturn(Optional.of(chTemplate));

        NonPlayerCreatureEditorQuestion uut = new NonPlayerCreatureEditorQuestion(applicationContext, repositoryBundle, commService);
        Output result = uut.prompt(webSocketContext);

        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("New name")));
    }

    @Test
    void testPromptHearts() {
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MEDIT_MODEL, 65L,
            MEDIT_STATE, MeditState.HEARTS
        ));
        when(mudCharacterPrototypeRepository.findById(eq(65L))).thenReturn(Optional.of(chTemplate));

        NonPlayerCreatureEditorQuestion uut = new NonPlayerCreatureEditorQuestion(applicationContext, repositoryBundle, commService);
        Output result = uut.prompt(webSocketContext);

        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("How many hearts")));
    }

    @Test
    void testAnswerName() {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_CHARACTER, 1L);
        attributes.put(MEDIT_MODEL, 65L);

        when(webSocketContext.getAttributes()).thenReturn(attributes);
        when(mudCharacterRepository.findById(eq(1L))).thenReturn(Optional.of(ch));
        when(mudCharacterPrototypeRepository.findById(eq(65L))).thenReturn(Optional.of(chTemplate));

        NonPlayerCreatureEditorQuestion uut = new NonPlayerCreatureEditorQuestion(applicationContext, repositoryBundle, commService);
        Response result = uut.answer(webSocketContext, new Input("n"));

        assertEquals(question, result.getNext());
        assertEquals(MeditState.NAME, attributes.get(MEDIT_STATE));
        assertTrue(attributes.containsKey(MEDIT_MODEL));
    }

    @Test
    void testAnswerNameInput() {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_CHARACTER, 1L);
        attributes.put(MEDIT_MODEL, 65L);
        attributes.put(MEDIT_STATE, MeditState.NAME);

        when(webSocketContext.getAttributes()).thenReturn(attributes);
        when(mudCharacterRepository.findById(eq(1L))).thenReturn(Optional.of(ch));
        when(mudCharacterPrototypeRepository.findById(eq(65L))).thenReturn(Optional.of(chTemplate));

        NonPlayerCreatureEditorQuestion uut = new NonPlayerCreatureEditorQuestion(applicationContext, repositoryBundle, commService);
        Response result = uut.answer(webSocketContext, new Input("a test npc"));

        assertEquals(question, result.getNext());
        assertFalse(attributes.containsKey(MEDIT_STATE));
        assertTrue(attributes.containsKey(MEDIT_MODEL));

        verify(chTempComponent).setName(eq("a test npc"));
    }

    @Test
    void testAnswerPronouns() {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_CHARACTER, 1L);
        attributes.put(MEDIT_MODEL, 65L);

        when(webSocketContext.getAttributes()).thenReturn(attributes);
        when(mudCharacterRepository.findById(eq(1L))).thenReturn(Optional.of(ch));
        when(mudCharacterPrototypeRepository.findById(eq(65L))).thenReturn(Optional.of(chTemplate));

        NonPlayerCreatureEditorQuestion uut = new NonPlayerCreatureEditorQuestion(applicationContext, repositoryBundle, commService);
        Response result = uut.answer(webSocketContext, new Input("p"));

        assertEquals(pronounEditorQuestion, result.getNext());
        assertFalse(attributes.containsKey(MEDIT_STATE));
        assertTrue(attributes.containsKey(MEDIT_MODEL));
    }

    @Test
    void testAnswerHearts() {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_CHARACTER, 1L);
        attributes.put(MEDIT_MODEL, 65L);

        when(webSocketContext.getAttributes()).thenReturn(attributes);
        when(mudCharacterRepository.findById(eq(1L))).thenReturn(Optional.of(ch));
        when(mudCharacterPrototypeRepository.findById(eq(65L))).thenReturn(Optional.of(chTemplate));

        NonPlayerCreatureEditorQuestion uut = new NonPlayerCreatureEditorQuestion(applicationContext, repositoryBundle, commService);
        Response result = uut.answer(webSocketContext, new Input("h"));

        assertEquals(question, result.getNext());
        assertEquals(MeditState.HEARTS, attributes.get(MEDIT_STATE));
        assertTrue(attributes.containsKey(MEDIT_MODEL));
    }

    @ParameterizedTest
    @ValueSource(strings = { "1", "2", "3", "4" })
    void testAnswerHeartsInput(String input) {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_CHARACTER, 1L);
        attributes.put(MEDIT_MODEL, 65L);
        attributes.put(MEDIT_STATE, MeditState.HEARTS);

        when(webSocketContext.getAttributes()).thenReturn(attributes);
        when(mudCharacterRepository.findById(eq(1L))).thenReturn(Optional.of(ch));
        when(mudCharacterPrototypeRepository.findById(eq(65L))).thenReturn(Optional.of(chTemplate));

        NonPlayerCreatureEditorQuestion uut = new NonPlayerCreatureEditorQuestion(applicationContext, repositoryBundle, commService);
        Response result = uut.answer(webSocketContext, new Input(input));
        int hearts = Integer.parseInt(input);

        assertEquals(question, result.getNext());
        assertFalse(attributes.containsKey(MEDIT_STATE));
        assertTrue(attributes.containsKey(MEDIT_MODEL));

        verify(chTempComponent).setHitPoints(eq(hearts * 10));
        verify(chTempComponent).setMaxHitPoints(eq(hearts * 10));
    }

    @ParameterizedTest
    @ValueSource(strings = { "-1", "0", "5", "6", "what" })
    void testAnswerHeartsInputInvalid(String input) {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_CHARACTER, 1L);
        attributes.put(MEDIT_MODEL, 65L);
        attributes.put(MEDIT_STATE, MeditState.HEARTS);

        when(webSocketContext.getAttributes()).thenReturn(attributes);
        when(mudCharacterRepository.findById(eq(1L))).thenReturn(Optional.of(ch));
        when(mudCharacterPrototypeRepository.findById(eq(65L))).thenReturn(Optional.of(chTemplate));

        NonPlayerCreatureEditorQuestion uut = new NonPlayerCreatureEditorQuestion(applicationContext, repositoryBundle, commService);
        Response result = uut.answer(webSocketContext, new Input(input));

        assertEquals(question, result.getNext());
        assertTrue(result.getFeedback().orElseThrow().getOutput().stream().anyMatch(line -> line.contains("Choose a number of hearts from 1 to 4.")));
        assertTrue(attributes.containsKey(MEDIT_STATE));
        assertTrue(attributes.containsKey(MEDIT_MODEL));

        verify(chTempComponent, never()).setHitPoints(eq(30));
        verify(chTempComponent, never()).setMaxHitPoints(eq(30));
    }

    @Test
    void testAnswerExit() {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_CHARACTER, 1L);
        attributes.put(MEDIT_MODEL, 65L);

        when(webSocketContext.getAttributes()).thenReturn(attributes);
        when(mudCharacterRepository.findById(eq(1L))).thenReturn(Optional.of(ch));
        when(mudCharacterPrototypeRepository.findById(eq(65L))).thenReturn(Optional.of(chTemplate));
        when(room.getId()).thenReturn(100L);

        NonPlayerCreatureEditorQuestion uut = new NonPlayerCreatureEditorQuestion(applicationContext, repositoryBundle, commService);
        Response result = uut.answer(webSocketContext, new Input("x"));
        Output lines = result.getFeedback().orElseThrow();

        assertEquals(commandQuestion, result.getNext());
        assertTrue(lines.getOutput().stream().anyMatch(line -> line.contains("Saved changes")));
        assertFalse(attributes.containsKey(MEDIT_MODEL));
        assertFalse(attributes.containsKey(MEDIT_STATE));

        verify(mudCharacterPrototypeRepository).save(eq(chTemplate));
        verify(commService).sendToRoom(eq(100L), any(Output.class), eq(ch));
    }
}
