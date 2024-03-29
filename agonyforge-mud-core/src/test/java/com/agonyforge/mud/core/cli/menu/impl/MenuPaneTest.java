package com.agonyforge.mud.core.cli.menu.impl;

import com.agonyforge.mud.core.cli.Color;
import com.agonyforge.mud.core.cli.menu.impl.MenuItem;
import com.agonyforge.mud.core.cli.menu.impl.MenuPane;
import com.agonyforge.mud.core.cli.menu.impl.MenuPrompt;
import com.agonyforge.mud.core.cli.menu.impl.MenuTitle;
import com.agonyforge.mud.core.web.model.Output;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MenuPaneTest {
    @Mock
    private MenuTitle title;

    @Mock
    private MenuItem item1;

    @Mock
    private MenuItem item2;

    @Mock
    private MenuPrompt prompt;

    @Test
    void testRenderEmpty() {
        MenuPane uut = new MenuPane();
        Output result = uut.render(Color.BLUE, Color.RED);

        assertEquals(0, result.getOutput().size());
    }

    @Test
    void testRenderTitle() {
        String line1 = "********";
        String line2 = "* Test *";
        String line3 = "********";

        doReturn(new Output(Arrays.asList(line1, line2, line3))).when(title).render(any(Color[].class));

        MenuPane uut = new MenuPane();
        uut.setTitle(title);

        Output result = uut.render(Color.BLUE, Color.RED);

        assertEquals(3, result.getOutput().size());
        assertEquals(line1, result.getOutput().get(0));
        assertEquals(line2, result.getOutput().get(1));
        assertEquals(line3, result.getOutput().get(2));

        verify(title).render(eq(Color.BLUE), eq(Color.RED));
        verify(item1, never()).render(any());
        verify(item2, never()).render(any());
        verify(prompt, never()).render(any());
    }

    @Test
    void testRenderItems() {
        String line1 = "1) One";
        String line2 = "2) Two";

        doReturn(new Output(line1)).when(item1).render(any(Color[].class));
        doReturn(new Output(line2)).when(item2).render(any(Color[].class));

        MenuPane uut = new MenuPane();
        uut.getItems().add(item1);
        uut.getItems().add(item2);

        Output result = uut.render(Color.BLUE, Color.RED);

        assertEquals(2, result.getOutput().size());
        assertEquals(line1, result.getOutput().get(0));
        assertEquals(line2, result.getOutput().get(1));

        verify(title, never()).render(any());
        verify(item1).render(eq(Color.BLUE), eq(Color.RED));
        verify(item2).render(eq(Color.BLUE), eq(Color.RED));
        verify(prompt, never()).render(any());
    }

    @Test
    void testRenderPrompt() {
        String line1 = "Choose: ";

        doReturn(new Output(line1)).when(prompt).render(any(Color[].class));

        MenuPane uut = new MenuPane();
        uut.setPrompt(prompt);

        Output result = uut.render(Color.BLUE, Color.RED);

        assertEquals(1, result.getOutput().size());
        assertEquals(line1, result.getOutput().get(0));

        verify(title, never()).render(any());
        verify(item1, never()).render(any());
        verify(item2, never()).render(any());
        verify(prompt).render(eq(Color.BLUE), eq(Color.RED));
    }

    @Test
    void testRenderAll() {
        String line1 = "********";
        String line2 = "* Test *";
        String line3 = "********";
        String line4 = "1) One";
        String line5 = "2) Two";
        String line6 = "Choose: ";

        doReturn(new Output(Arrays.asList(line1, line2, line3))).when(title).render(any(Color[].class));
        doReturn(new Output(line4)).when(item1).render(any(Color[].class));
        doReturn(new Output(line5)).when(item2).render(any(Color[].class));
        doReturn(new Output(line6)).when(prompt).render(any(Color[].class));

        MenuPane uut = new MenuPane();
        uut.setTitle(title);
        uut.getItems().add(item1);
        uut.getItems().add(item2);
        uut.setPrompt(prompt);

        Output result = uut.render(Color.BLUE, Color.RED);

        assertEquals(6, result.getOutput().size());
        assertEquals(line1, result.getOutput().get(0));
        assertEquals(line2, result.getOutput().get(1));
        assertEquals(line3, result.getOutput().get(2));
        assertEquals(line4, result.getOutput().get(3));
        assertEquals(line5, result.getOutput().get(4));
        assertEquals(line6, result.getOutput().get(5));

        verify(title).render(eq(Color.BLUE), eq(Color.RED));
        verify(item1).render(eq(Color.BLUE), eq(Color.RED));
        verify(item2).render(eq(Color.BLUE), eq(Color.RED));
        verify(prompt).render(eq(Color.BLUE), eq(Color.RED));
    }
}
