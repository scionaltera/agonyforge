package com.agonyforge.mud.demo.cli.question.ingame.olc.item;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.*;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import com.agonyforge.mud.demo.model.repository.MudItemPrototypeRepository;
import com.agonyforge.mud.demo.service.CommService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static com.agonyforge.mud.demo.cli.question.ingame.olc.item.ItemEditorQuestion.IEDIT_MODEL;
import static com.agonyforge.mud.demo.cli.question.ingame.olc.item.ItemEditorQuestion.IEDIT_STATE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemEditorQuestionTest {
    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private CommService commService;

    @Mock
    private WebSocketContext wsContext;

    @Mock
    private MudItemPrototypeRepository itemPrototypeRepository;

    @Mock
    private MudItemTemplate itemProto;

    @Mock
    private ItemComponent itemComponent;

    @Mock
    private MudRoom room;

    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private MudCharacter ch;

    @Mock
    private CharacterComponent characterComponent;

    @Mock
    private LocationComponent locationComponent;

    @Mock
    private Question question;

    @Mock
    private Question nextQuestion;

    @BeforeEach
    void setUp() {
        lenient().when(repositoryBundle.getItemPrototypeRepository()).thenReturn(itemPrototypeRepository);
        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);

        lenient().when(applicationContext.getBean(eq("itemEditorQuestion"), eq(Question.class))).thenReturn(question);
    }

    @Test
    void testPrompt() {
        when(wsContext.getAttributes()).thenReturn(Map.of(IEDIT_MODEL, 42L));
        when(itemProto.getItem()).thenReturn(itemComponent);
        when(itemPrototypeRepository.findById(eq(42L))).thenReturn(Optional.of(itemProto));
        when(itemProto.getId()).thenReturn(42L);

        ItemEditorQuestion uut = new ItemEditorQuestion(applicationContext, repositoryBundle, commService);
        Output result = uut.prompt(wsContext);

        assertTrue(result.getOutput().size() > 10);
        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("Item Editor - 42")));
    }

    @Test
    void testPromptNamelist() {
        when(wsContext.getAttributes()).thenReturn(Map.of(
            IEDIT_MODEL, 42L,
            IEDIT_STATE, "IEDIT.NAMES"
        ));
        when(itemPrototypeRepository.findById(eq(42L))).thenReturn(Optional.of(itemProto));

        ItemEditorQuestion uut = new ItemEditorQuestion(applicationContext, repositoryBundle, commService);
        Output result = uut.prompt(wsContext);

        assertEquals(1, result.getOutput().size());
        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("New name list: ")));
    }

    @Test
    void testPromptShortDesc() {
        when(wsContext.getAttributes()).thenReturn(Map.of(
            IEDIT_MODEL, 42L,
            IEDIT_STATE, "IEDIT.SHORT_DESC"
        ));
        when(itemPrototypeRepository.findById(eq(42L))).thenReturn(Optional.of(itemProto));

        ItemEditorQuestion uut = new ItemEditorQuestion(applicationContext, repositoryBundle, commService);
        Output result = uut.prompt(wsContext);

        assertEquals(1, result.getOutput().size());
        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("New short description: ")));
    }

    @Test
    void testPromptLongDesc() {
        when(wsContext.getAttributes()).thenReturn(Map.of(
            IEDIT_MODEL, 42L,
            IEDIT_STATE, "IEDIT.LONG_DESC"
        ));
        when(itemPrototypeRepository.findById(eq(42L))).thenReturn(Optional.of(itemProto));

        ItemEditorQuestion uut = new ItemEditorQuestion(applicationContext, repositoryBundle, commService);
        Output result = uut.prompt(wsContext);

        assertEquals(1, result.getOutput().size());
        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("New long description: ")));
    }

    @Test
    void testAnswerNamelist() {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_CHARACTER, 12L);
        attributes.put(IEDIT_MODEL, 42L);
        attributes.put(IEDIT_STATE, "IEDIT.NAMES");
        when(wsContext.getAttributes()).thenReturn(attributes);
        when(itemProto.getItem()).thenReturn(itemComponent);
        when(itemPrototypeRepository.findById(eq(42L))).thenReturn(Optional.of(itemProto));
        when(characterRepository.findById(12L)).thenReturn(Optional.of(ch));

        ItemEditorQuestion uut = new ItemEditorQuestion(applicationContext, repositoryBundle, commService);
        Response result = uut.answer(wsContext, new Input("really big sword"));

        verify(itemComponent).setNameList(anySet());
        verify(itemComponent, never()).setLongDescription(anyString());
        verify(itemComponent, never()).setLongDescription(anyString());
        assertNull(attributes.get(IEDIT_STATE));
        assertEquals(question, result.getNext());
    }

    @Test
    void testAnswerShortDesc() {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_CHARACTER, 12L);
        attributes.put(IEDIT_MODEL, 42L);
        attributes.put(IEDIT_STATE, "IEDIT.SHORT_DESC");
        when(wsContext.getAttributes()).thenReturn(attributes);
        when(itemProto.getItem()).thenReturn(itemComponent);
        when(itemPrototypeRepository.findById(eq(42L))).thenReturn(Optional.of(itemProto));
        when(characterRepository.findById(12L)).thenReturn(Optional.of(ch));

        ItemEditorQuestion uut = new ItemEditorQuestion(applicationContext, repositoryBundle, commService);
        Response result = uut.answer(wsContext, new Input("a really big sword"));

        verify(itemComponent, never()).setNameList(anySet());
        verify(itemComponent).setShortDescription(anyString());
        verify(itemComponent, never()).setLongDescription(anyString());
        assertNull(attributes.get(IEDIT_STATE));
        assertEquals(question, result.getNext());
    }

    @Test
    void testAnswerLongDesc() {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_CHARACTER, 12L);
        attributes.put(IEDIT_MODEL, 42L);
        attributes.put(IEDIT_STATE, "IEDIT.LONG_DESC");
        when(wsContext.getAttributes()).thenReturn(attributes);
        when(itemProto.getItem()).thenReturn(itemComponent);
        when(itemPrototypeRepository.findById(eq(42L))).thenReturn(Optional.of(itemProto));
        when(characterRepository.findById(12L)).thenReturn(Optional.of(ch));

        ItemEditorQuestion uut = new ItemEditorQuestion(applicationContext, repositoryBundle, commService);
        Response result = uut.answer(wsContext, new Input("A really big sword has been dropped here."));

        verify(itemComponent, never()).setNameList(anySet());
        verify(itemComponent, never()).setShortDescription(anyString());
        verify(itemComponent).setLongDescription(anyString());
        assertNull(attributes.get(IEDIT_STATE));
        assertEquals(question, result.getNext());
    }

    @Test
    void testNoStateAnswerNamelist() {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_CHARACTER, 12L);
        attributes.put(IEDIT_MODEL, 42L);
        when(wsContext.getAttributes()).thenReturn(attributes);
        when(itemPrototypeRepository.findById(eq(42L))).thenReturn(Optional.of(itemProto));
        when(characterRepository.findById(12L)).thenReturn(Optional.of(ch));

        ItemEditorQuestion uut = new ItemEditorQuestion(applicationContext, repositoryBundle, commService);
        Response response = uut.answer(wsContext, new Input("n"));

        assertEquals("IEDIT.NAMES", attributes.get(IEDIT_STATE));
        assertEquals(question, response.getNext());
    }

    @Test
    void testNoStateAnswerShortDesc() {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_CHARACTER, 12L);
        attributes.put(IEDIT_MODEL, 42L);
        when(wsContext.getAttributes()).thenReturn(attributes);
        when(itemPrototypeRepository.findById(eq(42L))).thenReturn(Optional.of(itemProto));
        when(characterRepository.findById(12L)).thenReturn(Optional.of(ch));

        ItemEditorQuestion uut = new ItemEditorQuestion(applicationContext, repositoryBundle, commService);
        Response response = uut.answer(wsContext, new Input("s"));

        assertEquals("IEDIT.SHORT_DESC", attributes.get(IEDIT_STATE));
        assertEquals(question, response.getNext());
    }

    @Test
    void testNoStateAnswerLongDesc() {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_CHARACTER, 12L);
        attributes.put(IEDIT_MODEL, 42L);
        when(wsContext.getAttributes()).thenReturn(attributes);
        when(itemPrototypeRepository.findById(eq(42L))).thenReturn(Optional.of(itemProto));
        when(characterRepository.findById(12L)).thenReturn(Optional.of(ch));

        ItemEditorQuestion uut = new ItemEditorQuestion(applicationContext, repositoryBundle, commService);
        Response response = uut.answer(wsContext, new Input("l"));

        assertEquals("IEDIT.LONG_DESC", attributes.get(IEDIT_STATE));
        assertEquals(question, response.getNext());
    }

    @Test
    void testAnswerWearSlots() {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_CHARACTER, 12L);
        attributes.put(IEDIT_MODEL, 42L);
        when(wsContext.getAttributes()).thenReturn(attributes);
        when(itemPrototypeRepository.findById(eq(42L))).thenReturn(Optional.of(itemProto));
        when(characterRepository.findById(12L)).thenReturn(Optional.of(ch));
        when(applicationContext.getBean(eq("itemWearSlotsEditorQuestion"), eq(Question.class))).thenReturn(nextQuestion);

        ItemEditorQuestion uut = new ItemEditorQuestion(applicationContext, repositoryBundle, commService);
        Response response = uut.answer(wsContext, new Input("w"));

        assertEquals(nextQuestion, response.getNext());
    }

    @Test
    void testAnswerExit() {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_CHARACTER, 12L);
        attributes.put(IEDIT_MODEL, 42L);
        when(wsContext.getAttributes()).thenReturn(attributes);
        when(itemPrototypeRepository.findById(eq(42L))).thenReturn(Optional.of(itemProto));
        when(characterComponent.getName()).thenReturn("Name");
        when(room.getId()).thenReturn(100L);
        when(ch.getLocation()).thenReturn(locationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);
        when(ch.getCharacter()).thenReturn(characterComponent);
        when(characterRepository.findById(12L)).thenReturn(Optional.of(ch));
        when(applicationContext.getBean(eq("commandQuestion"), eq(Question.class))).thenReturn(nextQuestion);

        ItemEditorQuestion uut = new ItemEditorQuestion(applicationContext, repositoryBundle, commService);
        Response response = uut.answer(wsContext, new Input("x"));

        verify(itemPrototypeRepository).save(eq(itemProto));
        verify(commService).sendToRoom(eq(100L), any(Output.class), eq(ch));

        assertNull(attributes.get(IEDIT_MODEL));
        assertNull(attributes.get(IEDIT_STATE));

        assertEquals(nextQuestion, response.getNext());
    }
}
