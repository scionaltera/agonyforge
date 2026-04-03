package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.model.constant.Pronoun;
import com.agonyforge.mud.demo.model.constant.WearSlot;
import com.agonyforge.mud.demo.model.impl.*;
import com.agonyforge.mud.demo.model.repository.MudItemPrototypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateCommandTest extends CommandTestBoilerplate {
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
    private MudItem item;

    @Mock
    private MudRoom room;

    @Mock
    private Binding itemTemplateBinding;

    @BeforeEach
    void setUp() {
        lenient().when(repositoryBundle.getItemPrototypeRepository()).thenReturn(itemPrototypeRepository);

        when(itemTemplateBinding.asItemTemplate()).thenReturn(itemTemplate);
    }

    @Test
    void testCreateItem() {
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

        Question response = uut.execute(question, webSocketContext, List.of(commandBinding, itemTemplateBinding), output);

        assertEquals(question, response);

        verify(itemLocationComponent).setWorn(eq(EnumSet.noneOf(WearSlot.class)));
        verify(itemLocationComponent).setHeld(eq(ch));
        verify(itemLocationComponent).setRoom(eq(null));
        verify(itemRepository).save(eq(item));
        verify(commService).sendToRoom(anyLong(), any(Output.class), eq(ch));
    }
}
