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
import java.util.Optional;
import java.util.UUID;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CharacterViewQuestionTest {
    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private Principal principal;

    @Mock
    private Session session;

    @Mock
    private MudCharacter ch;

    @Mock
    private Question question;

    @Test
    void testPrompt() {
        UUID chId = UUID.randomUUID();
        String characterName = "Scion";

        when(session.getAttribute(eq(MUD_CHARACTER))).thenReturn(chId);
        when(characterRepository.getById(eq(chId))).thenReturn(Optional.of(ch));
        when(ch.getName()).thenReturn(characterName);

        CharacterViewQuestion uut = new CharacterViewQuestion(applicationContext, characterRepository);
        Output result = uut.prompt(principal, session);

        assertEquals(6, result.getOutput().size());
        assertTrue(result.getOutput().get(0).contains("Character Sheet"));
        assertTrue(result.getOutput().get(1).contains(characterName));
        assertEquals("", result.getOutput().get(2));
        assertTrue(result.getOutput().get(3).contains("Play"));
        assertTrue(result.getOutput().get(4).contains("Delete"));
        assertTrue(result.getOutput().get(5).contains("selection"));
    }

    @Test
    void testPromptNoCharacter() {
        when(characterRepository.getById(any())).thenReturn(Optional.empty());

        CharacterViewQuestion uut = new CharacterViewQuestion(applicationContext, characterRepository);
        Output result = uut.prompt(principal, session);

        assertTrue(result.getOutput().get(0).contains("[red]"));
        assertTrue(result.getOutput().get(0).contains("error has been reported"));
    }

    @Test
    void testAnswerPlay() {
        when(applicationContext.getBean(eq("echoQuestion"), eq(Question.class))).thenReturn(question);

        CharacterViewQuestion uut = new CharacterViewQuestion(applicationContext, characterRepository);
        Response result = uut.answer(principal, session, new Input("p"));

        assertEquals(question, result.getNext());
    }

    @Test
    void testAnswerDelete() {
        when(applicationContext.getBean(eq("characterDeleteQuestion"), eq(Question.class))).thenReturn(question);

        CharacterViewQuestion uut = new CharacterViewQuestion(applicationContext, characterRepository);
        Response result = uut.answer(principal, session, new Input("d"));

        assertEquals(question, result.getNext());

        verify(characterRepository, never()).delete(any());
    }

    @Test
    void testAnswerUnknown() {
        CharacterViewQuestion uut = new CharacterViewQuestion(applicationContext, characterRepository);
        Response result = uut.answer(principal, session, new Input("x"));
        Output output = result.getFeedback().orElseThrow();

        assertEquals(uut, result.getNext());
        assertTrue(output.getOutput().get(0).contains("try again"));
    }
}
