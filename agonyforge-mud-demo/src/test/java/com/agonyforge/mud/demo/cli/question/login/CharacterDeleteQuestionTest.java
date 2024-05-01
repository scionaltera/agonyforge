package com.agonyforge.mud.demo.cli.question.login;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import com.agonyforge.mud.demo.model.repository.MudItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
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
    private MudCharacterRepository characterRepository;

    @Mock
    private MudItemRepository itemRepository;

    @Mock
    private MudCharacter ch;

    @Mock
    private WebSocketContext webSocketContext;

    @Mock
    private Question question;

    @BeforeEach
    void setUp() {
        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        lenient().when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
    }

    @Test
    void testPrompt() {
        UUID chId = UUID.randomUUID();
        String chName = "Scion";
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_CHARACTER, chId);

        when(webSocketContext.getAttributes()).thenReturn(attributes);
        when(characterRepository.getById(eq(chId), eq(true))).thenReturn(Optional.of(ch));
        when(ch.getName()).thenReturn(chName);

        CharacterDeleteQuestion uut = new CharacterDeleteQuestion(applicationContext, repositoryBundle);
        Output result = uut.prompt(webSocketContext);

        assertEquals(1, result.getOutput().size());
        assertTrue(result.getOutput().get(0).contains("[red]"));
        assertTrue(result.getOutput().get(0).contains("SURE"));
        assertTrue(result.getOutput().get(0).contains(chName));
    }

    @Test
    void testPromptNoCharacter() {
        UUID chId = UUID.randomUUID();
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_CHARACTER, chId);

        when(webSocketContext.getAttributes()).thenReturn(attributes);
        when(characterRepository.getById(any(), eq(true))).thenReturn(Optional.empty());

        CharacterDeleteQuestion uut = new CharacterDeleteQuestion(applicationContext, repositoryBundle);
        Output result = uut.prompt(webSocketContext);

        assertEquals(1, result.getOutput().size());
        assertTrue(result.getOutput().get(0).contains("[red]"));
        assertTrue(result.getOutput().get(0).contains("error has been reported"));
    }

    @Test
    void testAnswerYes() {
        UUID chId = UUID.randomUUID();
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_CHARACTER, chId);

        when(webSocketContext.getAttributes()).thenReturn(attributes);
        when(characterRepository.getById(eq(chId), eq(true))).thenReturn(Optional.of(ch));
        when(applicationContext.getBean(eq("characterMenuQuestion"), eq(Question.class))).thenReturn(question);

        CharacterDeleteQuestion uut = new CharacterDeleteQuestion(applicationContext, repositoryBundle);
        Response result = uut.answer(webSocketContext, new Input("y"));
        Output output = result.getFeedback().orElseThrow();

        assertEquals(question, result.getNext());
        assertEquals(1, output.getOutput().size());
        assertTrue(output.getOutput().get(0).contains("[red]"));
        assertTrue(output.getOutput().get(0).contains("deleted"));

        verify(characterRepository).delete(eq(ch));
    }

    @Test
    void testAnswerNo() {
        UUID chId = UUID.randomUUID();
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_CHARACTER, chId);

        when(webSocketContext.getAttributes()).thenReturn(attributes);
        when(characterRepository.getById(eq(chId), eq(true))).thenReturn(Optional.of(ch));
        when(applicationContext.getBean(eq("characterMenuQuestion"), eq(Question.class))).thenReturn(question);

        CharacterDeleteQuestion uut = new CharacterDeleteQuestion(applicationContext, repositoryBundle);
        Response result = uut.answer(webSocketContext, new Input("n"));
        Output output = result.getFeedback().orElseThrow();

        assertEquals(question, result.getNext());
        assertEquals(1, output.getOutput().size());
        assertTrue(output.getOutput().get(0).contains("[green]"));
        assertTrue(output.getOutput().get(0).contains("safe"));

        verify(characterRepository, never()).delete(eq(ch));
    }
}
