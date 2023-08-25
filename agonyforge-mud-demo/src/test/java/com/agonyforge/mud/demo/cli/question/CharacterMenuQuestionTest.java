package com.agonyforge.mud.demo.cli.question;

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

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CharacterMenuQuestionTest {
    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private Principal principal;

    @Mock
    private Question question;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private MudItemRepository itemRepository;

    @Mock
    private MudCharacter mudCharacter;

    @Mock
    private WebSocketContext webSocketContext;

    @BeforeEach
    void setUp() {
        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        lenient().when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
    }

    @Test
    void testPromptNoCharacters() {
        String principalName = "principal";

        when(principal.getName()).thenReturn(principalName);
        when(webSocketContext.getPrincipal()).thenReturn(principal);

        CharacterMenuQuestion uut = new CharacterMenuQuestion(
            applicationContext,
            repositoryBundle);
        Output result = uut.prompt(webSocketContext);
        Optional<String> itemOptional = result.getOutput()
                .stream()
                .filter(line -> line.contains("New Character"))
                .findFirst();

        assertEquals(7, result.getOutput().size());
        assertTrue(itemOptional.isPresent());

        verify(characterRepository).getByUser(eq(principalName));
    }

    @Test
    void testPromptWithCharacters() {
        String principalName = "principal";
        String characterName = "Scion";

        when(principal.getName()).thenReturn(principalName);
        when(webSocketContext.getPrincipal()).thenReturn(principal);
        when(mudCharacter.getName()).thenReturn(characterName);
        when(characterRepository.getByUser(eq(principalName))).thenReturn(List.of(mudCharacter));

        CharacterMenuQuestion uut = new CharacterMenuQuestion(
            applicationContext,
            repositoryBundle);
        Output result = uut.prompt(webSocketContext);
        Optional<String> newCharacterLineOptional = result.getOutput()
            .stream()
            .filter(line -> line.contains("New Character"))
            .findAny();
        Optional<String> characterNameLineOptional = result.getOutput()
            .stream()
            .filter(line -> line.contains(characterName) && line.contains("1"))
            .findAny();

        assertEquals(8, result.getOutput().size());
        assertTrue(newCharacterLineOptional.isPresent());
        assertTrue(characterNameLineOptional.isPresent());

        verify(characterRepository).getByUser(eq(principalName));
    }

    @Test
    void testPromptTwice() {
        String principalName = "principal";

        when(principal.getName()).thenReturn(principalName);
        when(webSocketContext.getPrincipal()).thenReturn(principal);

        CharacterMenuQuestion uut = new CharacterMenuQuestion(
            applicationContext,
            repositoryBundle);
        uut.prompt(webSocketContext);
        Output result = uut.prompt(webSocketContext);

        List<String> lines = result.getOutput()
            .stream()
            .filter(line -> line.contains("New Character"))
            .toList();

        assertEquals(1, lines.size());
    }

    @Test
    void testAnswerEmpty() {
        CharacterMenuQuestion uut = new CharacterMenuQuestion(
            applicationContext,
            repositoryBundle);

        when(webSocketContext.getPrincipal()).thenReturn(principal);
        when(applicationContext.getBean(eq("characterMenuQuestion"), eq(Question.class))).thenReturn(uut);

        Response result = uut.answer(webSocketContext, new Input(""));

        assertEquals(uut, result.getNext());
        verify(webSocketContext, never()).getAttributes();
    }

    @Test
    void testAnswerNew() {
        when(webSocketContext.getPrincipal()).thenReturn(principal);
        when(applicationContext.getBean(eq("characterNameQuestion"), eq(Question.class))).thenReturn(question);

        CharacterMenuQuestion uut = new CharacterMenuQuestion(
            applicationContext,
            repositoryBundle);
        Response result = uut.answer(webSocketContext, new Input("n"));

        assertEquals(question, result.getNext());

        verify(webSocketContext, never()).getAttributes();
    }

    @Test
    void testAnswerExisting() {
        String principalName = "principal";
        UUID characterId = UUID.randomUUID();
        String characterName = "Scion";
        Map<String, Object> attributes = new HashMap<>();

        when(principal.getName()).thenReturn(principalName);
        when(webSocketContext.getPrincipal()).thenReturn(principal);
        when(webSocketContext.getAttributes()).thenReturn(attributes);
        when(mudCharacter.getId()).thenReturn(characterId);
        when(mudCharacter.getName()).thenReturn(characterName);
        when(characterRepository.getByUser(eq(principalName))).thenReturn(List.of(mudCharacter));
        when(applicationContext.getBean(eq("characterViewQuestion"), eq(Question.class))).thenReturn(question);

        CharacterMenuQuestion uut = new CharacterMenuQuestion(
            applicationContext,
            repositoryBundle);

        Response result = uut.answer(webSocketContext, new Input("1"));

        assertEquals(question, result.getNext());
        assertEquals(characterId, attributes.get(MUD_CHARACTER));
    }
}
