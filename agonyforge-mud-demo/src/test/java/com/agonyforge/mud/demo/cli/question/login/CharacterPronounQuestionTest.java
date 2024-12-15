package com.agonyforge.mud.demo.cli.question.login;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.constant.Pronoun;
import com.agonyforge.mud.demo.model.impl.MudCharacterPrototype;
import com.agonyforge.mud.demo.model.repository.MudCharacterPrototypeRepository;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import com.agonyforge.mud.demo.model.repository.MudItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.*;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_PCHARACTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CharacterPronounQuestionTest {
    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private Question question;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private MudCharacterPrototypeRepository characterPrototypeRepository;

    @Mock
    private MudItemRepository itemRepository;

    @Mock
    private WebSocketContext webSocketContext;

    @Mock
    private MudCharacterPrototype chProto;

    private final Random random = new Random();

    @BeforeEach
    void setUp() {
        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        lenient().when(repositoryBundle.getCharacterPrototypeRepository()).thenReturn(characterPrototypeRepository);
        lenient().when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
    }

    @Test
    void testPrompt() {
        CharacterPronounQuestion uut = new CharacterPronounQuestion(applicationContext, repositoryBundle);
        Output result = uut.prompt(webSocketContext);

        assertEquals(6 + Pronoun.values().length, result.getOutput().size());
        Arrays.stream(Pronoun.values())
            .forEach(pronoun -> assertTrue(result.getOutput()
                .stream()
                .anyMatch(line -> line.contains(pronoun.getObject()))));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "x"
    })
    void testAnswerInvalid() {
        when(applicationContext.getBean(eq("characterPronounQuestion"), eq(Question.class))).thenReturn(question);

        CharacterPronounQuestion uut = new CharacterPronounQuestion(applicationContext, repositoryBundle);
        Response result = uut.answer(webSocketContext, new Input(""));
        Output output = result.getFeedback().orElseThrow();

        assertEquals(question, result.getNext());
        assertTrue(output.getOutput().get(0).contains("Please choose one of the menu options."));
    }

    @Test
    void testAnswerValid() {
        Long chId = random.nextLong();

        when(applicationContext.getBean(eq("characterStatQuestion"), eq(Question.class))).thenReturn(question);
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_PCHARACTER, chId
        ));
        when(characterPrototypeRepository.findById(eq(chId))).thenReturn(Optional.of(chProto));

        CharacterPronounQuestion uut = new CharacterPronounQuestion(applicationContext, repositoryBundle);
        Response result = uut.answer(webSocketContext, new Input("2"));
        Output output = result.getFeedback().orElseThrow();

        verify(chProto).setPronoun(eq(Pronoun.SHE));
        verify(characterPrototypeRepository).save(eq(chProto));

        assertEquals(question, result.getNext());
        assertEquals(0, output.getOutput().size());
    }
}
