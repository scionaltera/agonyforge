package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.models.dynamodb.constant.Stat;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.impl.MudRoom;
import com.agonyforge.mud.models.dynamodb.constant.Pronoun;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudItemRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudRoomRepository;
import com.agonyforge.mud.models.dynamodb.service.CommService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static com.agonyforge.mud.demo.cli.CharacterViewQuestion.START_ROOM;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CharacterViewQuestionTest {
    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private MudItemRepository itemRepository;

    @Mock
    private MudRoomRepository roomRepository;

    @Mock
    private CommService commService;

    @Mock
    private MudCharacter ch;

    @Mock
    private MudCharacter chInstance;

    @Mock
    private MudRoom room;

    @Mock
    private Question question;

    @Mock
    private WebSocketContext wsContext;

    @Captor
    private ArgumentCaptor<MudCharacter> characterCaptor;

    @Captor
    private ArgumentCaptor<Output> outputCaptor;

    @BeforeEach
    void setUp() {
        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        lenient().when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
        lenient().when(repositoryBundle.getRoomRepository()).thenReturn(roomRepository);
    }

    @Test
    void testPrompt() {
        UUID chId = UUID.randomUUID();
        String characterName = "Scion";
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_CHARACTER, chId);

        when(wsContext.getAttributes()).thenReturn(attributes);
        when(characterRepository.getById(eq(chId), eq(true))).thenReturn(Optional.of(ch));
        when(ch.getName()).thenReturn(characterName);
        when(ch.getPronoun()).thenReturn(Pronoun.SHE);

        CharacterViewQuestion uut = new CharacterViewQuestion(applicationContext, repositoryBundle, commService);
        Output result = uut.prompt(wsContext);

        int i = 0;
        assertEquals(19, result.getOutput().size());
        assertTrue(result.getOutput().get(i++).contains("CHARACTER SHEET"));
        assertTrue(result.getOutput().get(i++).contains(characterName));
        assertTrue(result.getOutput().get(i++).contains(Pronoun.SHE.getObject()));
        i++; // blank line
        assertTrue(result.getOutput().get(i).contains("Stats"));
        assertTrue(result.getOutput().get(i++).contains("Efforts"));
        assertTrue(result.getOutput().get(i).contains("Basic"));
        assertTrue(result.getOutput().get(i++).contains("STR:"));
        assertTrue(result.getOutput().get(i).contains("Weapons & Tools"));
        assertTrue(result.getOutput().get(i++).contains("DEX:"));
        assertTrue(result.getOutput().get(i).contains("Guns"));
        assertTrue(result.getOutput().get(i++).contains("CON:"));
        assertTrue(result.getOutput().get(i).contains("Energy & Magic"));
        assertTrue(result.getOutput().get(i++).contains("INT:"));
        assertTrue(result.getOutput().get(i).contains("Ultimate"));
        assertTrue(result.getOutput().get(i++).contains("WIS:"));
        assertTrue(result.getOutput().get(i++).contains("CHA:"));
        i++; // blank line
        assertTrue(result.getOutput().get(i++).contains("Health:"));
        assertTrue(result.getOutput().get(i++).contains("DEF:"));
        assertEquals("", result.getOutput().get(i++));
        assertTrue(result.getOutput().get(i++).contains("Play"));
        assertTrue(result.getOutput().get(i++).contains("Delete"));
        assertTrue(result.getOutput().get(i++).contains("Go back"));
        assertTrue(result.getOutput().get(i).contains("selection"));
    }

    @Test
    void testPromptNoCharacter() {
        when(characterRepository.getById(any(), anyBoolean())).thenReturn(Optional.empty());

        CharacterViewQuestion uut = new CharacterViewQuestion(applicationContext, repositoryBundle, commService);
        Output result = uut.prompt(wsContext);

        assertTrue(result.getOutput().get(0).contains("[red]"));
        assertTrue(result.getOutput().get(0).contains("error has been reported"));
    }

    @Test
    void testAnswerPlay() {
        UUID chId = UUID.randomUUID();
        String wsSessionId = UUID.randomUUID().toString();
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_CHARACTER, chId);

        when(wsContext.getAttributes()).thenReturn(attributes);
        when(wsContext.getSessionId()).thenReturn(wsSessionId);
        when(ch.buildInstance()).thenReturn(chInstance);
        when(characterRepository.getById(eq(chId), eq(true))).thenReturn(Optional.of(ch));
        when(roomRepository.getById(eq(START_ROOM))).thenReturn(Optional.of(room));
        when(applicationContext.getBean(eq("commandQuestion"), eq(Question.class))).thenReturn(question);

        CharacterViewQuestion uut = new CharacterViewQuestion(applicationContext, repositoryBundle, commService);
        Response result = uut.answer(wsContext, new Input("p"));

        verify(characterRepository).save(characterCaptor.capture());
        verify(commService).sendToAll(any(WebSocketContext.class), outputCaptor.capture(), eq(chInstance));

        MudCharacter instance = characterCaptor.getValue();

        verify(instance).setRoomId(eq(100L));
        verify(instance).setWebSocketSession(eq(wsSessionId));

        Output announcement = outputCaptor.getValue();

        assertTrue(announcement.getOutput().get(0).contains("has entered the game"));

        assertEquals(question, result.getNext());
    }

    @Test
    void testAnswerDelete() {
        when(applicationContext.getBean(eq("characterDeleteQuestion"), eq(Question.class))).thenReturn(question);

        CharacterViewQuestion uut = new CharacterViewQuestion(applicationContext, repositoryBundle, commService);
        Response result = uut.answer(wsContext, new Input("d"));

        assertEquals(question, result.getNext());

        verify(characterRepository, never()).delete(any());
    }

    @Test
    void testAnswerBack() {
        when(applicationContext.getBean(eq("characterMenuQuestion"), eq(Question.class))).thenReturn(question);

        CharacterViewQuestion uut = new CharacterViewQuestion(applicationContext, repositoryBundle, commService);
        Response result = uut.answer(wsContext, new Input("b"));

        assertEquals(question, result.getNext());

        verify(characterRepository, never()).delete(any());
    }

    @Test
    void testAnswerUnknown() {
        CharacterViewQuestion uut = new CharacterViewQuestion(applicationContext, repositoryBundle, commService);
        Response result = uut.answer(wsContext, new Input("x"));
        Output output = result.getFeedback().orElseThrow();

        assertEquals(uut, result.getNext());
        assertTrue(output.getOutput().get(0).contains("try again"));
    }
}
