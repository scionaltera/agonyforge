package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.constant.Pronoun;
import com.agonyforge.mud.demo.model.constant.WearSlot;
import com.agonyforge.mud.demo.model.impl.*;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import com.agonyforge.mud.demo.model.repository.MudItemPrototypeRepository;
import com.agonyforge.mud.demo.model.repository.MudItemRepository;
import com.agonyforge.mud.demo.service.CommService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateCommandTest {
    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private CommService commService;

    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private MudCharacter ch;

    @Mock
    private LocationComponent chLocationComponent, itemLocationComponent;

    @Mock
    private CharacterComponent characterComponent;

    @Mock
    private ItemComponent itemComponent;

    @Mock
    private MudItemPrototypeRepository itemPrototypeRepository;

    @Mock
    private MudItemTemplate itemTemplate;

    @Mock
    private MudItemRepository itemRepository;

    @Mock
    private MudItem item;

    @Mock
    private MudRoom room;

    @Mock
    private WebSocketContext wsContext;

    @Mock
    private Question question;

    @BeforeEach
    void setUp() {
        lenient().when(repositoryBundle.getItemPrototypeRepository()).thenReturn(itemPrototypeRepository);

        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        lenient().when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
    }

    @Test
    void testNoArgs() {
        when(wsContext.getAttributes()).thenReturn(Map.of(MUD_CHARACTER, 1L));
        when(characterRepository.findById(eq(1L))).thenReturn(Optional.of(ch));
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);

        Output output = new Output();
        CreateCommand uut = new CreateCommand(repositoryBundle, commService, applicationContext);

        Question response = uut.execute(question, wsContext, List.of("CRE"), new Input("cre"), output);

        assertEquals(question, response);

        verifyNoInteractions(itemRepository);
        verifyNoInteractions(commService);
    }

    @Test
    void testCreateInvalidItem() {
        when(wsContext.getAttributes()).thenReturn(Map.of(MUD_CHARACTER, 1L));
        when(characterRepository.findById(eq(1L))).thenReturn(Optional.of(ch));
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);

        Output output = new Output();
        CreateCommand uut = new CreateCommand(repositoryBundle, commService, applicationContext);

        Question response = uut.execute(question, wsContext, List.of("CRE", "404"), new Input("cre 404"), output);

        assertEquals(question, response);

        verify(itemPrototypeRepository).findById(eq(404L));
        verifyNoMoreInteractions(itemPrototypeRepository);
        verifyNoInteractions(itemRepository);
        verifyNoInteractions(commService);
    }

    @Test
    void testCreateItem() {
        when(wsContext.getAttributes()).thenReturn(Map.of(MUD_CHARACTER, 1L));
        when(characterRepository.findById(eq(1L))).thenReturn(Optional.of(ch));
        when(itemPrototypeRepository.findById(eq(200L))).thenReturn(Optional.of(itemTemplate));
        when(itemRepository.save(eq(item))).thenReturn(item);
        when(itemTemplate.buildInstance()).thenReturn(item);
        when(item.getItem()).thenReturn(itemComponent);
        when(item.getLocation()).thenReturn(itemLocationComponent);
        when(ch.getCharacter()).thenReturn(characterComponent);
        when(ch.getCharacter().getPronoun()).thenReturn(Pronoun.SHE);
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);

        Output output = new Output();
        CreateCommand uut = new CreateCommand(repositoryBundle, commService, applicationContext);

        Question response = uut.execute(question, wsContext, List.of("CRE", "200"), new Input("cre 200"), output);

        assertEquals(question, response);

        verify(itemPrototypeRepository).findById(eq(200L));
        verify(itemLocationComponent).setWorn(eq(EnumSet.noneOf(WearSlot.class)));
        verify(itemLocationComponent).setHeld(eq(ch));
        verify(itemLocationComponent).setRoom(eq(null));
        verify(itemRepository).save(eq(item));
        verify(commService).sendToRoom(anyLong(), any(Output.class), eq(ch));
    }
}
