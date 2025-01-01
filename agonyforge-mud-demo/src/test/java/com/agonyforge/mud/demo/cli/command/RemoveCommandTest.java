package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.constant.WearSlot;
import com.agonyforge.mud.demo.model.impl.CharacterComponent;
import com.agonyforge.mud.demo.model.impl.ItemComponent;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudItem;
import com.agonyforge.mud.demo.model.constant.Pronoun;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import com.agonyforge.mud.demo.model.repository.MudItemRepository;
import com.agonyforge.mud.demo.model.repository.MudRoomRepository;
import com.agonyforge.mud.demo.service.CommService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.*;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RemoveCommandTest {
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
    private WebSocketContext webSocketContext;

    @Mock
    private Question question;

    @Mock
    private MudCharacter ch;

    @Mock
    private CharacterComponent characterComponent;

    @Mock
    private MudItem target;

    @Mock
    private ItemComponent itemComponent;

    private final Random random = new Random();

    @BeforeEach
    void setUp() {
        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        lenient().when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
        lenient().when(repositoryBundle.getRoomRepository()).thenReturn(roomRepository);
    }

    @Test
    void testRemoveNoArg() {
        Long chId = random.nextLong();

        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));

        Output output = new Output();
        RemoveCommand uut = new RemoveCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("REMOVE"),
            new Input("rem"),
            output);

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("What would you like to remove?"));
    }

    @Test
    void testRemoveNoTarget() {
        Long chId = random.nextLong();

        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));

        Output output = new Output();
        RemoveCommand uut = new RemoveCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("REMOVE", "HAT"),
            new Input("rem hat"),
            output);

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("You aren't wearing anything like that"));
    }

    @Test
    void testRemoveTarget() {
        Long chId = random.nextLong();

        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(characterComponent.getPronoun()).thenReturn(Pronoun.SHE);
        when(ch.getCharacter()).thenReturn(characterComponent);
        when(itemRepository.getByChId(eq(ch.getId()))).thenReturn(List.of(target));
        when(target.getItem()).thenReturn(itemComponent);
        when(target.getItem().getNameList()).thenReturn(Set.of("test", "hat"));
        when(target.getItem().getShortDescription()).thenReturn("a test hat");
        when(target.getWorn()).thenReturn(WearSlot.HEAD);

        Output output = new Output();
        RemoveCommand uut = new RemoveCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("REMOVE", "HAT"),
            new Input("rem hat"),
            output);

        verify(target).setWorn(eq(null));
        verify(itemRepository).save(any(MudItem.class));
        verify(commService).sendToRoom(
            eq(webSocketContext),
            anyLong(),
            any(Output.class));

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("You remove a test hat"));
    }
}
