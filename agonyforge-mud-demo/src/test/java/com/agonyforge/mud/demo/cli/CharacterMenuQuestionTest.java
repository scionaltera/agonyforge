package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.session.Session;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CharacterMenuQuestionTest {
    @Mock
    private Principal principal;

    @Mock
    private Session session;

    @Mock
    private Question question;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private MudCharacter mudCharacter;

    @Test
    void testPromptNoCharacters() {
        String principalName = "principal";

        when(principal.getName()).thenReturn(principalName);

        CharacterMenuQuestion uut = new CharacterMenuQuestion(
            applicationContext,
            characterRepository);
        Output result = uut.prompt(principal, session);
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
        when(mudCharacter.getName()).thenReturn(characterName);
        when(characterRepository.getByUser(eq(principalName))).thenReturn(List.of(mudCharacter));

        CharacterMenuQuestion uut = new CharacterMenuQuestion(
            applicationContext,
            characterRepository);
        Output result = uut.prompt(principal, session);
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

        CharacterMenuQuestion uut = new CharacterMenuQuestion(
            applicationContext,
            characterRepository);
        uut.prompt(principal, session);
        Output result = uut.prompt(principal, session);

        List<String> lines = result.getOutput()
            .stream()
            .filter(line -> line.contains("New Character"))
            .collect(Collectors.toList());

        assertEquals(1, lines.size());
    }

    @Test
    void testAnswerEmpty() {
        CharacterMenuQuestion uut = new CharacterMenuQuestion(
            applicationContext,
            characterRepository);

        when(applicationContext.getBean(eq("characterMenuQuestion"), eq(Question.class))).thenReturn(uut);

        Response result = uut.answer(principal, session, new Input(""));

        assertEquals(uut, result.getNext());
        verifyNoInteractions(session);
    }

    @Test
    void testAnswerNew() {
        CharacterMenuQuestion uut = new CharacterMenuQuestion(
            applicationContext,
            characterRepository);

        when(applicationContext.getBean(eq("characterNameQuestion"), eq(Question.class))).thenReturn(question);

        Response result = uut.answer(principal, session, new Input("n"));

        assertEquals(question, result.getNext());
        verifyNoInteractions(session);
    }

    @Test
    void testAnswerExisting() {
        String principalName = "principal";
        UUID characterId = UUID.randomUUID();
        String characterName = "Scion";

        when(principal.getName()).thenReturn(principalName);
        when(mudCharacter.getId()).thenReturn(characterId);
        when(mudCharacter.getName()).thenReturn(characterName);
        when(characterRepository.getByUser(eq(principalName))).thenReturn(List.of(mudCharacter));
        when(applicationContext.getBean(eq("characterViewQuestion"), eq(Question.class))).thenReturn(question);

        CharacterMenuQuestion uut = new CharacterMenuQuestion(
            applicationContext,
            characterRepository);

        Response result = uut.answer(principal, session, new Input("1"));

        assertEquals(question, result.getNext());
        verify(session).setAttribute(eq(MUD_CHARACTER), eq(characterId));
    }
}
