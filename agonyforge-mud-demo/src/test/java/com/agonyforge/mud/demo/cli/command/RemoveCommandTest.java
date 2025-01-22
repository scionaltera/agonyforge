package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.constant.WearSlot;
import com.agonyforge.mud.demo.model.impl.*;
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

    @Mock
    private LocationComponent chLocationComponent, targetLocationComponent;

    @Mock
    private MudRoom room;

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
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);

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
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);

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
        when(room.getId()).thenReturn(100L);
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);
        when(ch.getCharacter()).thenReturn(characterComponent);
        when(itemRepository.findByLocationHeld(eq(ch))).thenReturn(List.of(target));
        when(target.getItem()).thenReturn(itemComponent);
        when(target.getItem().getNameList()).thenReturn(Set.of("test", "hat"));
        when(target.getItem().getShortDescription()).thenReturn("a test hat");
        when(target.getLocation()).thenReturn(targetLocationComponent);
        when(target.getLocation().getWorn()).thenReturn(EnumSet.of(WearSlot.HEAD));

        Output output = new Output();
        RemoveCommand uut = new RemoveCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("REMOVE", "HAT"),
            new Input("rem hat"),
            output);

        verify(targetLocationComponent).setHeld(eq(ch));
        verify(targetLocationComponent).setRoom(eq(null));
        verify(targetLocationComponent).setWorn(eq(EnumSet.noneOf(WearSlot.class)));
        verify(itemRepository).save(any(MudItem.class));
        verify(commService).sendToRoom(
            anyLong(),
            any(Output.class),
            eq(ch));

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("You remove a test hat"));
    }
}
