package com.agonyforge.mud.demo.cli.question.login;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.CharacterComponent;
import com.agonyforge.mud.demo.model.impl.MudCharacterTemplate;
import com.agonyforge.mud.demo.model.repository.MudCharacterPrototypeRepository;
import com.agonyforge.mud.demo.model.repository.MudItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.*;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_PCHARACTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CharacterDeleteQuestionTest {
    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private MudCharacterPrototypeRepository characterPrototypeRepository;

    @Mock
    private MudItemRepository itemRepository;

    @Mock
    private MudCharacterTemplate ch;

    @Mock
    private CharacterComponent characterComponent;

    @Mock
    private WebSocketContext webSocketContext;

    @Mock
    private Question question;

    private final Random random = new Random();

    @BeforeEach
    void setUp() {
        lenient().when(repositoryBundle.getCharacterPrototypeRepository()).thenReturn(characterPrototypeRepository);
        lenient().when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
    }

    @Test
    void testPrompt() {
        Long chId = random.nextLong();
        String chName = "Scion";
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_PCHARACTER, chId);

        when(webSocketContext.getAttributes()).thenReturn(attributes);
        when(characterPrototypeRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(characterComponent.getName()).thenReturn(chName);
        when(ch.getCharacter()).thenReturn(characterComponent);

        CharacterDeleteQuestion uut = new CharacterDeleteQuestion(applicationContext, repositoryBundle);
        Output result = uut.prompt(webSocketContext);

        assertEquals(1, result.getOutput().size());
        assertTrue(result.getOutput().get(0).contains("[red]"));
        assertTrue(result.getOutput().get(0).contains("SURE"));
        assertTrue(result.getOutput().get(0).contains(chName));
    }

    @Test
    void testPromptNoCharacter() {
        Long chId = random.nextLong();
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_PCHARACTER, chId);

        when(webSocketContext.getAttributes()).thenReturn(attributes);
        when(characterPrototypeRepository.findById(any())).thenReturn(Optional.empty());

        CharacterDeleteQuestion uut = new CharacterDeleteQuestion(applicationContext, repositoryBundle);
        Output result = uut.prompt(webSocketContext);

        assertEquals(1, result.getOutput().size());
        assertTrue(result.getOutput().get(0).contains("[red]"));
        assertTrue(result.getOutput().get(0).contains("error has been reported"));
    }

    @Test
    void testAnswerYes() {
        Long chId = random.nextLong();
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_PCHARACTER, chId);

        when(webSocketContext.getAttributes()).thenReturn(attributes);
        when(characterPrototypeRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(applicationContext.getBean(eq("characterMenuQuestion"), eq(Question.class))).thenReturn(question);

        CharacterDeleteQuestion uut = new CharacterDeleteQuestion(applicationContext, repositoryBundle);
        Response result = uut.answer(webSocketContext, new Input("y"));
        Output output = result.getFeedback().orElseThrow();

        assertEquals(question, result.getNext());
        assertEquals(1, output.getOutput().size());
        assertTrue(output.getOutput().get(0).contains("[red]"));
        assertTrue(output.getOutput().get(0).contains("deleted"));

        verify(characterPrototypeRepository).delete(eq(ch));
    }

    @Test
    void testAnswerNo() {
        Long chId = random.nextLong();
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_PCHARACTER, chId);

        when(webSocketContext.getAttributes()).thenReturn(attributes);
        when(characterPrototypeRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(applicationContext.getBean(eq("characterMenuQuestion"), eq(Question.class))).thenReturn(question);

        CharacterDeleteQuestion uut = new CharacterDeleteQuestion(applicationContext, repositoryBundle);
        Response result = uut.answer(webSocketContext, new Input("n"));
        Output output = result.getFeedback().orElseThrow();

        assertEquals(question, result.getNext());
        assertEquals(1, output.getOutput().size());
        assertTrue(output.getOutput().get(0).contains("[green]"));
        assertTrue(output.getOutput().get(0).contains("safe"));

        verify(characterPrototypeRepository, never()).delete(eq(ch));
    }
}
