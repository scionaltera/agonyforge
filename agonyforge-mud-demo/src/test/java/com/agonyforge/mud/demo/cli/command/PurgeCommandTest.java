package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.constant.Pronoun;
import com.agonyforge.mud.demo.model.impl.*;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import com.agonyforge.mud.demo.model.repository.MudItemRepository;
import com.agonyforge.mud.demo.service.CommService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PurgeCommandTest {
    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private MudItemRepository itemRepository;

    @Mock
    private CommService commService;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private WebSocketContext wsContext;

    @Mock
    private Question question;

    @Mock
    private MudCharacter ch, target;

    @Mock
    private MudItem item;

    @Mock
    private MudRoom room;

    @Mock
    private LocationComponent chLocationComponent, itemLocationComponent;

    @Mock
    private ItemComponent itemComponent;

    @Mock
    private CharacterComponent characterComponent, targetCharacterComponent;

    @Mock
    private PlayerComponent targetPlayerComponent;

    @Mock
    private Binding commandBinding, itemBinding, characterBinding;

    @BeforeEach
    void setUp() {
        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        lenient().when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);

        when(wsContext.getAttributes()).thenReturn(Map.of(MUD_CHARACTER, 1L));
        when(characterRepository.findById(eq(1L))).thenReturn(Optional.of(ch));
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);
        when(ch.getCharacter()).thenReturn(characterComponent);
        when(itemBinding.asItem()).thenReturn(item);
        when(characterBinding.asCharacter()).thenReturn(target);
        lenient().when(ch.getCharacter().getPronoun()).thenReturn(Pronoun.SHE);
        lenient().when(characterRepository.findByLocationRoom(eq(room))).thenReturn(List.of(ch, target));
        lenient().when(target.getCharacter()).thenReturn(targetCharacterComponent);
        lenient().when(target.getCharacter().getName()).thenReturn("Greedo");
    }

    @Test
    void testPurgeInventoryItem() {
        when(itemRepository.findByLocationHeld(eq(ch))).thenReturn(List.of(item));
        when(item.getLocation()).thenReturn(itemLocationComponent);
        when(item.getItem()).thenReturn(itemComponent);
        when(item.getItem().getNameList()).thenReturn(Set.of("test"));

        Output output = new Output();
        PurgeCommand uut = new PurgeCommand(repositoryBundle, commService, applicationContext);

        Question result = uut.execute(question, wsContext, List.of(commandBinding, itemBinding), output);

        assertEquals(question, result);

        verify(itemRepository).delete(eq(item));
        verify(commService).sendToRoom(anyLong(), any(Output.class), eq(ch));
    }

    @Test
    void testPurgeRoomItem() {
        when(itemRepository.findByLocationRoom(eq(room))).thenReturn(List.of(item));
        when(item.getItem()).thenReturn(itemComponent);
        when(item.getItem().getNameList()).thenReturn(Set.of("test"));

        Output output = new Output();
        PurgeCommand uut = new PurgeCommand(repositoryBundle, commService, applicationContext);

        Question result = uut.execute(question, wsContext, List.of(commandBinding, itemBinding), output);

        assertEquals(question, result);

        verify(itemRepository).delete(eq(item));
        verify(commService).sendToRoom(anyLong(), any(Output.class), eq(ch));
    }

    @Test
    void testPurgeRoomCharacter() {
        Output output = new Output();
        PurgeCommand uut = new PurgeCommand(repositoryBundle, commService, applicationContext);

        Question result = uut.execute(question, wsContext, List.of(commandBinding, characterBinding), output);

        assertEquals(question, result);

        verify(characterRepository).delete(eq(target));
        verify(commService).sendToRoom(anyLong(), any(Output.class), eq(ch));
    }

    @Test
    void testPurgeRoomPlayer() {
        when(target.getPlayer()).thenReturn(targetPlayerComponent);

        Output output = new Output();
        PurgeCommand uut = new PurgeCommand(repositoryBundle, commService, applicationContext);

        Question result = uut.execute(question, wsContext, List.of(commandBinding, characterBinding), output);

        assertEquals(question, result);

        verify(characterRepository, never()).delete(eq(target));
        verify(commService, never()).sendToRoom(anyLong(), any(Output.class), eq(ch));
    }
}
