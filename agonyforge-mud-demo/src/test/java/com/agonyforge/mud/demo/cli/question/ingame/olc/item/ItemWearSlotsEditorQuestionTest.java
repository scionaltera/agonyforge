package com.agonyforge.mud.demo.cli.question.ingame.olc.item;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.constant.WearSlot;
import com.agonyforge.mud.demo.model.impl.ItemComponent;
import com.agonyforge.mud.demo.model.impl.MudItemTemplate;
import com.agonyforge.mud.demo.model.repository.MudItemPrototypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.agonyforge.mud.demo.cli.question.ingame.olc.item.ItemEditorQuestion.IEDIT_MODEL;
import static com.agonyforge.mud.demo.cli.question.ingame.olc.item.ItemEditorQuestion.IEDIT_STATE;
import static com.agonyforge.mud.demo.cli.question.ingame.olc.item.ItemWearSlotsEditorQuestion.IEDIT_SLOT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemWearSlotsEditorQuestionTest {
    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private MudItemPrototypeRepository itemPrototypeRepository;

    @Mock
    private WebSocketContext wsContext;

    @Mock
    private MudItemTemplate itemProto;

    @Mock
    private ItemComponent itemComponent;

    @Mock
    private Question question;

    @Mock
    private Question nextQuestion;

    @Test
    void testPromptDefault() {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(IEDIT_MODEL, 42L);

        when(repositoryBundle.getItemPrototypeRepository()).thenReturn(itemPrototypeRepository);
        when(itemProto.getItem()).thenReturn(itemComponent);
        when(itemPrototypeRepository.findById(eq(42L))).thenReturn(Optional.of(itemProto));
        when(wsContext.getAttributes()).thenReturn(attributes);
        when(itemProto.getItem().getWearSlots()).thenReturn(EnumSet.noneOf(WearSlot.class));

        ItemWearSlotsEditorQuestion uut = new ItemWearSlotsEditorQuestion(applicationContext, repositoryBundle);
        Output result = uut.prompt(wsContext);

        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("Item Wear Slots")));
        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("make your selection")));
        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("false")));
        assertTrue(result.getOutput().stream().noneMatch(line -> line.contains("true")));
        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("Wear Slot Mode")));
        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("Exit")));
    }

    @Test
    void testPromptToggleSlotOn() {
        WearSlot eyes = WearSlot.EYES;
        EnumSet<WearSlot> slots = EnumSet.noneOf(WearSlot.class);
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(IEDIT_MODEL, 42L);
        attributes.put(IEDIT_STATE, IEDIT_SLOT);
        attributes.put(IEDIT_SLOT, eyes);

        when(repositoryBundle.getItemPrototypeRepository()).thenReturn(itemPrototypeRepository);
        when(itemProto.getItem()).thenReturn(itemComponent);
        when(itemPrototypeRepository.findById(eq(42L))).thenReturn(Optional.of(itemProto));
        when(itemPrototypeRepository.save(eq(itemProto))).thenReturn(itemProto);
        when(wsContext.getAttributes()).thenReturn(attributes);
        when(itemProto.getItem().getWearSlots()).thenReturn(slots);

        ItemWearSlotsEditorQuestion uut = new ItemWearSlotsEditorQuestion(applicationContext, repositoryBundle);
        Output result = uut.prompt(wsContext);

        assertTrue(slots.contains(eyes));
        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("true")));
    }

    @Test
    void testPromptToggleSlotOff() {
        WearSlot eyes = WearSlot.EYES;
        EnumSet<WearSlot> slots = EnumSet.of(eyes);
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(IEDIT_MODEL, 42L);
        attributes.put(IEDIT_STATE, IEDIT_SLOT);
        attributes.put(IEDIT_SLOT, eyes);

        when(repositoryBundle.getItemPrototypeRepository()).thenReturn(itemPrototypeRepository);
        when(itemProto.getItem()).thenReturn(itemComponent);
        when(itemPrototypeRepository.findById(eq(42L))).thenReturn(Optional.of(itemProto));
        when(itemPrototypeRepository.save(eq(itemProto))).thenReturn(itemProto);
        when(wsContext.getAttributes()).thenReturn(attributes);
        when(itemProto.getItem().getWearSlots()).thenReturn(slots);

        ItemWearSlotsEditorQuestion uut = new ItemWearSlotsEditorQuestion(applicationContext, repositoryBundle);
        Output result = uut.prompt(wsContext);

        assertFalse(slots.contains(eyes));
        assertTrue(result.getOutput().stream().noneMatch(line -> line.contains("true")));
    }

    @Test
    void testAnswerSlot() {
        EnumSet<WearSlot> slots = EnumSet.noneOf(WearSlot.class);
        Map<String, Object> attributes = new HashMap<>();

        when(applicationContext.getBean(eq("itemWearSlotsEditorQuestion"), eq(Question.class))).thenReturn(question);
        when(wsContext.getAttributes()).thenReturn(attributes);
        when(itemProto.getItem()).thenReturn(itemComponent);
        when(itemComponent.getWearSlots()).thenReturn(slots);

        ItemWearSlotsEditorQuestion uut = new ItemWearSlotsEditorQuestion(applicationContext, repositoryBundle);
        uut.populateMenuItems(itemProto);
        Response result = uut.answer(wsContext, new Input("13"));

        assertEquals(IEDIT_SLOT, attributes.get(IEDIT_STATE));
        assertEquals(WearSlot.EYES, attributes.get(IEDIT_SLOT));
        assertEquals(question, result.getNext());
    }

    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "RedundantOperationOnEmptyContainer"})
    @Test
    void testAnswerSlotInvalid() {
        EnumSet<WearSlot> slots = EnumSet.noneOf(WearSlot.class);
        Map<String, Object> attributes = new HashMap<>();

        when(applicationContext.getBean(eq("itemWearSlotsEditorQuestion"), eq(Question.class))).thenReturn(question);
        when(itemProto.getItem()).thenReturn(itemComponent);
        when(itemComponent.getWearSlots()).thenReturn(slots);

        ItemWearSlotsEditorQuestion uut = new ItemWearSlotsEditorQuestion(applicationContext, repositoryBundle);
        uut.populateMenuItems(itemProto);
        Response result = uut.answer(wsContext, new Input("F"));

        assertNull(attributes.get(IEDIT_STATE));
        assertNull(attributes.get(IEDIT_SLOT));

        assertTrue(result.getFeedback().orElseThrow().getOutput().stream().anyMatch(line -> line.contains("choose a menu item")));
        assertEquals(question, result.getNext());
    }

    @Test
    void testAnswerExit() {
        Map<String, Object> attributes = new HashMap<>();

        when(applicationContext.getBean(eq("itemEditorQuestion"), eq(Question.class))).thenReturn(nextQuestion);
        when(wsContext.getAttributes()).thenReturn(attributes);

        ItemWearSlotsEditorQuestion uut = new ItemWearSlotsEditorQuestion(applicationContext, repositoryBundle);
        Response result = uut.answer(wsContext, new Input("x"));

        assertFalse(attributes.containsKey(IEDIT_STATE));
        assertFalse(attributes.containsKey(IEDIT_SLOT));
        assertEquals(nextQuestion, result.getNext());
    }
}
