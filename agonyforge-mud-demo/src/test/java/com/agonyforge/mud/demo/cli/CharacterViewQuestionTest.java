package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
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
    private MudCharacter ch;

    @Mock
    private MudCharacter chInstance;

    @Mock
    private Question question;

    @Mock
    private WebSocketContext wsContext;

    @Captor
    private ArgumentCaptor<MudCharacter> characterCaptor;

    @Test
    void testPrompt() {
        UUID chId = UUID.randomUUID();
        String characterName = "Scion";
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_CHARACTER, chId);

        when(wsContext.getAttributes()).thenReturn(attributes);
        when(characterRepository.getById(eq(chId), eq(true))).thenReturn(Optional.of(ch));
        when(ch.getName()).thenReturn(characterName);

        CharacterViewQuestion uut = new CharacterViewQuestion(applicationContext, characterRepository);
        Output result = uut.prompt(wsContext);

        assertEquals(7, result.getOutput().size());
        assertTrue(result.getOutput().get(0).contains("Character Sheet"));
        assertTrue(result.getOutput().get(1).contains(characterName));
        assertEquals("", result.getOutput().get(2));
        assertTrue(result.getOutput().get(3).contains("Play"));
        assertTrue(result.getOutput().get(4).contains("Delete"));
        assertTrue(result.getOutput().get(5).contains("Go back"));
        assertTrue(result.getOutput().get(6).contains("selection"));
    }

    @Test
    void testPromptNoCharacter() {
        when(characterRepository.getById(any(), anyBoolean())).thenReturn(Optional.empty());

        CharacterViewQuestion uut = new CharacterViewQuestion(applicationContext, characterRepository);
        Output result = uut.prompt(wsContext);

        assertTrue(result.getOutput().get(0).contains("[red]"));
        assertTrue(result.getOutput().get(0).contains("error has been reported"));
    }

    @Test
    void testAnswerPlay() {
        UUID chId = UUID.randomUUID();
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_CHARACTER, chId);

        when(wsContext.getAttributes()).thenReturn(attributes);
        when(chInstance.isPrototype()).thenReturn(false);
        when(ch.buildInstance()).thenReturn(chInstance);
        when(characterRepository.getById(eq(chId), eq(true))).thenReturn(Optional.of(ch));
        when(applicationContext.getBean(eq("echoQuestion"), eq(Question.class))).thenReturn(question);

        CharacterViewQuestion uut = new CharacterViewQuestion(applicationContext, characterRepository);
        Response result = uut.answer(wsContext, new Input("p"));

        verify(characterRepository).save(characterCaptor.capture());

        MudCharacter instance = characterCaptor.getValue();

        assertFalse(instance.isPrototype());
        assertEquals(question, result.getNext());
    }

    @Test
    void testAnswerDelete() {
        when(applicationContext.getBean(eq("characterDeleteQuestion"), eq(Question.class))).thenReturn(question);

        CharacterViewQuestion uut = new CharacterViewQuestion(applicationContext, characterRepository);
        Response result = uut.answer(wsContext, new Input("d"));

        assertEquals(question, result.getNext());

        verify(characterRepository, never()).delete(any());
    }

    @Test
    void testAnswerBack() {
        when(applicationContext.getBean(eq("characterMenuQuestion"), eq(Question.class))).thenReturn(question);

        CharacterViewQuestion uut = new CharacterViewQuestion(applicationContext, characterRepository);
        Response result = uut.answer(wsContext, new Input("b"));

        assertEquals(question, result.getNext());

        verify(characterRepository, never()).delete(any());
    }

    @Test
    void testAnswerUnknown() {
        CharacterViewQuestion uut = new CharacterViewQuestion(applicationContext, characterRepository);
        Response result = uut.answer(wsContext, new Input("x"));
        Output output = result.getFeedback().orElseThrow();

        assertEquals(uut, result.getNext());
        assertTrue(output.getOutput().get(0).contains("try again"));
    }
}
