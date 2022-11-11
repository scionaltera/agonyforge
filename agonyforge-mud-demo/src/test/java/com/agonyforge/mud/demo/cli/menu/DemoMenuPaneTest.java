package com.agonyforge.mud.demo.cli.menu;

import com.agonyforge.mud.core.cli.Color;
import com.agonyforge.mud.core.web.model.Output;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DemoMenuPaneTest {
    @Mock
    private DemoMenuTitle title;

    @Mock
    private DemoMenuItem item1;

    @Mock
    private DemoMenuItem item2;

    @Mock
    private DemoMenuPrompt prompt;

    @Test
    void testRenderEmpty() {
        DemoMenuPane uut = new DemoMenuPane();
        Output result = uut.render(Color.BLUE, Color.RED);

        assertEquals(0, result.getOutput().size());
    }

    @Test
    void testRenderTitle() {
        String line1 = "********";
        String line2 = "* Test *";
        String line3 = "********";

        when(title.render(any())).thenReturn(new Output(line1, line2, line3));

        DemoMenuPane uut = new DemoMenuPane();
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

        when(item1.render(any())).thenReturn(new Output(line1));
        when(item2.render(any())).thenReturn(new Output(line2));

        DemoMenuPane uut = new DemoMenuPane();
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

        when(prompt.render(any())).thenReturn(new Output(line1));

        DemoMenuPane uut = new DemoMenuPane();
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

        when(title.render(any())).thenReturn(new Output(line1, line2, line3));
        when(item1.render(any())).thenReturn(new Output(line4));
        when(item2.render(any())).thenReturn(new Output(line5));
        when(prompt.render(any())).thenReturn(new Output(line6));

        DemoMenuPane uut = new DemoMenuPane();
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
