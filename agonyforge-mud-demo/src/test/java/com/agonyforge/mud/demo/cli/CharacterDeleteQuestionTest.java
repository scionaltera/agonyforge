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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CharacterDeleteQuestionTest {
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
        String chName = "Scion";

        when(session.getAttribute(eq(MUD_CHARACTER))).thenReturn(chId);
        when(characterRepository.getById(eq(chId))).thenReturn(Optional.of(ch));
        when(ch.getName()).thenReturn(chName);

        CharacterDeleteQuestion uut = new CharacterDeleteQuestion(applicationContext, characterRepository);
        Output result = uut.prompt(principal, session);

        assertEquals(1, result.getOutput().size());
        assertTrue(result.getOutput().get(0).contains("[red]"));
        assertTrue(result.getOutput().get(0).contains("SURE"));
        assertTrue(result.getOutput().get(0).contains(chName));
    }

    @Test
    void testPromptNoCharacter() {
        UUID chId = UUID.randomUUID();

        when(session.getAttribute(eq(MUD_CHARACTER))).thenReturn(chId);
        when(characterRepository.getById(any())).thenReturn(Optional.empty());

        CharacterDeleteQuestion uut = new CharacterDeleteQuestion(applicationContext, characterRepository);
        Output result = uut.prompt(principal, session);

        assertEquals(1, result.getOutput().size());
        assertTrue(result.getOutput().get(0).contains("[red]"));
        assertTrue(result.getOutput().get(0).contains("error has been reported"));
    }

    @Test
    void testAnswerYes() {
        UUID chId = UUID.randomUUID();

        when(session.getAttribute(eq(MUD_CHARACTER))).thenReturn(chId);
        when(characterRepository.getById(eq(chId))).thenReturn(Optional.of(ch));

        CharacterDeleteQuestion uut = new CharacterDeleteQuestion(applicationContext, characterRepository);
        Response result = uut.answer(principal, session, new Input("y"));
        Output output = result.getFeedback().orElseThrow();

        assertEquals(1, output.getOutput().size());
        assertTrue(output.getOutput().get(0).contains("[red]"));
        assertTrue(output.getOutput().get(0).contains("deleted"));

        verify(characterRepository).delete(eq(ch));
    }
}
