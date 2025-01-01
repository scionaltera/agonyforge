package com.agonyforge.mud.demo.cli.question.login;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.constant.Stat;
import com.agonyforge.mud.demo.model.impl.CharacterComponent;
import com.agonyforge.mud.demo.model.impl.MudCharacterPrototype;
import com.agonyforge.mud.demo.model.repository.MudCharacterPrototypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_PCHARACTER;
import static com.agonyforge.mud.demo.cli.question.login.CharacterStatQuestion.STARTING_STATS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CharacterStatQuestionTest {
    private final Random random = new Random();

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private MudCharacterPrototypeRepository mudCharacterPrototypeRepository;

    @Mock
    private WebSocketContext wsContext;

    @Mock
    private MudCharacterPrototype chProto;

    @Mock
    private CharacterComponent characterComponent;

    @Mock
    private Question question;

    @Mock
    private Question nextQuestion;

    @BeforeEach
    void setUp() {
        lenient().when(applicationContext.getBean(eq("characterStatQuestion"), eq(Question.class))).thenReturn(question);
        lenient().when(applicationContext.getBean(eq("characterEffortQuestion"), eq(Question.class))).thenReturn(nextQuestion);

        lenient().when(repositoryBundle.getCharacterPrototypeRepository()).thenReturn(mudCharacterPrototypeRepository);
    }

    @Test
    void testPrompt() {
        Long chId = random.nextLong();

        when(chProto.getCharacter()).thenReturn(characterComponent);
        when(mudCharacterPrototypeRepository.findById(eq(chId))).thenReturn(Optional.of(chProto));
        when(wsContext.getAttributes()).thenReturn(Map.of(MUD_PCHARACTER, chId));

        CharacterStatQuestion uut = new CharacterStatQuestion(applicationContext, repositoryBundle);
        Output result = uut.prompt(wsContext);

        assertTrue(result.getOutput().size() >= 14);
    }

    @Test
    void testAdd() {
        Long chId = random.nextLong();

        when(chProto.getCharacter()).thenReturn(characterComponent);
        when(mudCharacterPrototypeRepository.findById(eq(chId))).thenReturn(Optional.of(chProto));
        when(wsContext.getAttributes()).thenReturn(Map.of(MUD_PCHARACTER, chId));

        CharacterStatQuestion uut = new CharacterStatQuestion(applicationContext, repositoryBundle);
        Response response = uut.answer(wsContext, new Input("1+"));

        verify(characterComponent).addBaseStat(eq(Stat.values()[0]), eq(1));
        verify(mudCharacterPrototypeRepository).save(eq(chProto));

        assertTrue(response.getFeedback().isPresent());
        assertEquals(question, response.getNext());
    }

    @Test
    void testAddTooMany() {
        Long chId = random.nextLong();

        lenient().when(characterComponent.getBaseStat(Stat.DEX)).thenReturn(STARTING_STATS);
        when(chProto.getCharacter()).thenReturn(characterComponent);
        when(mudCharacterPrototypeRepository.findById(eq(chId))).thenReturn(Optional.of(chProto));
        when(wsContext.getAttributes()).thenReturn(Map.of(MUD_PCHARACTER, chId));

        CharacterStatQuestion uut = new CharacterStatQuestion(applicationContext, repositoryBundle);
        Response response = uut.answer(wsContext, new Input("1+"));

        verify(characterComponent, never()).addBaseStat(any(Stat.class), anyInt());

        Output answer = response.getFeedback().orElseThrow();
        assertTrue(answer.getOutput().stream().anyMatch(line -> line.contains("[red]")));
        assertEquals(question, response.getNext());
    }

    @Test
    void testInvalidAddition() {
        Long chId = random.nextLong();

        when(chProto.getCharacter()).thenReturn(characterComponent);
        when(mudCharacterPrototypeRepository.findById(eq(chId))).thenReturn(Optional.of(chProto));
        when(wsContext.getAttributes()).thenReturn(Map.of(MUD_PCHARACTER, chId));

        CharacterStatQuestion uut = new CharacterStatQuestion(applicationContext, repositoryBundle);
        Response response = uut.answer(wsContext, new Input("+"));

        verify(characterComponent, never()).addBaseStat(any(Stat.class), anyInt());

        Output answer = response.getFeedback().orElseThrow();
        assertTrue(answer.getOutput().stream().anyMatch(line -> line.contains("[red]")));
        assertEquals(question, response.getNext());
    }

    @Test
    void testSubtract() {
        Long chId = random.nextLong();

        lenient().when(characterComponent.getBaseStat(Stat.DEX)).thenReturn(3);
        when(chProto.getCharacter()).thenReturn(characterComponent);
        when(mudCharacterPrototypeRepository.findById(eq(chId))).thenReturn(Optional.of(chProto));
        when(wsContext.getAttributes()).thenReturn(Map.of(MUD_PCHARACTER, chId));

        CharacterStatQuestion uut = new CharacterStatQuestion(applicationContext, repositoryBundle);
        Response response = uut.answer(wsContext, new Input("2-"));

        verify(characterComponent).addBaseStat(eq(Stat.DEX), eq(-1));
        verify(mudCharacterPrototypeRepository).save(eq(chProto));

        assertTrue(response.getFeedback().isPresent());
        assertEquals(question, response.getNext());
    }

    @Test
    void testSubtractTooMany() {
        Long chId = random.nextLong();

        when(chProto.getCharacter()).thenReturn(characterComponent);
        when(mudCharacterPrototypeRepository.findById(eq(chId))).thenReturn(Optional.of(chProto));
        when(wsContext.getAttributes()).thenReturn(Map.of(MUD_PCHARACTER, chId));

        CharacterStatQuestion uut = new CharacterStatQuestion(applicationContext, repositoryBundle);
        Response response = uut.answer(wsContext, new Input("1-"));

        verify(characterComponent, never()).addBaseStat(any(Stat.class), anyInt());

        Output answer = response.getFeedback().orElseThrow();
        assertTrue(answer.getOutput().stream().anyMatch(line -> line.contains("[red]")));
        assertEquals(question, response.getNext());
    }

    @Test
    void testInvalidSubtraction() {
        Long chId = random.nextLong();

        when(chProto.getCharacter()).thenReturn(characterComponent);
        when(mudCharacterPrototypeRepository.findById(eq(chId))).thenReturn(Optional.of(chProto));
        when(wsContext.getAttributes()).thenReturn(Map.of(MUD_PCHARACTER, chId));

        CharacterStatQuestion uut = new CharacterStatQuestion(applicationContext, repositoryBundle);
        Response response = uut.answer(wsContext, new Input("-"));

        verify(characterComponent, never()).addBaseStat(any(Stat.class), anyInt());

        Output answer = response.getFeedback().orElseThrow();
        assertTrue(answer.getOutput().stream().anyMatch(line -> line.contains("[red]")));
        assertEquals(question, response.getNext());
    }

    @Test
    void testGarbageInput() {
        Long chId = random.nextLong();

        when(chProto.getCharacter()).thenReturn(characterComponent);
        when(mudCharacterPrototypeRepository.findById(eq(chId))).thenReturn(Optional.of(chProto));
        when(wsContext.getAttributes()).thenReturn(Map.of(MUD_PCHARACTER, chId));

        CharacterStatQuestion uut = new CharacterStatQuestion(applicationContext, repositoryBundle);
        Response response = uut.answer(wsContext, new Input("F"));

        verify(characterComponent, never()).addBaseStat(any(Stat.class), anyInt());

        Output answer = response.getFeedback().orElseThrow();
        assertTrue(answer.getOutput().stream().anyMatch(line -> line.contains("[red]")));
        assertEquals(question, response.getNext());
    }

    @Test
    void testSave() {
        Long chId = random.nextLong();

        lenient().when(characterComponent.getBaseStat(Stat.DEX)).thenReturn(STARTING_STATS);
        when(chProto.getCharacter()).thenReturn(characterComponent);
        when(mudCharacterPrototypeRepository.findById(eq(chId))).thenReturn(Optional.of(chProto));
        when(wsContext.getAttributes()).thenReturn(Map.of(MUD_PCHARACTER, chId));

        CharacterStatQuestion uut = new CharacterStatQuestion(applicationContext, repositoryBundle);
        Response response = uut.answer(wsContext, new Input("s"));

        verify(mudCharacterPrototypeRepository).save(eq(chProto));

        assertTrue(response.getFeedback().isPresent());
        assertEquals(nextQuestion, response.getNext());
    }

    @Test
    void testSaveWhileUnallocated() {
        Long chId = random.nextLong();

        lenient().when(characterComponent.getBaseStat(Stat.DEX)).thenReturn(STARTING_STATS - 1);
        when(chProto.getCharacter()).thenReturn(characterComponent);
        when(mudCharacterPrototypeRepository.findById(eq(chId))).thenReturn(Optional.of(chProto));
        when(wsContext.getAttributes()).thenReturn(Map.of(MUD_PCHARACTER, chId));

        CharacterStatQuestion uut = new CharacterStatQuestion(applicationContext, repositoryBundle);
        Response response = uut.answer(wsContext, new Input("s"));

        verify(mudCharacterPrototypeRepository).save(eq(chProto));

        Output answer = response.getFeedback().orElseThrow();
        assertTrue(answer.getOutput().stream().anyMatch(line -> line.contains("allocate exactly")));
        assertEquals(question, response.getNext());
    }
}
