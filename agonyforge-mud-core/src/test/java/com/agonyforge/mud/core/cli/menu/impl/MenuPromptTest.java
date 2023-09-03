package com.agonyforge.mud.core.cli.menu.impl;

import com.agonyforge.mud.core.cli.menu.impl.MenuPrompt;
import com.agonyforge.mud.core.web.model.Output;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.agonyforge.mud.core.cli.Color.BLUE;
import static com.agonyforge.mud.core.cli.Color.RED;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MenuPromptTest {
    @Test
    void testRender() {
        MenuPrompt uut = new MenuPrompt();
        Output result = uut.render(BLUE, RED);

        assertEquals(2, result.getOutput().size());
        assertEquals("", result.getOutput().get(0));
        assertEquals("[red]Please [blue]make your selection[red]: ", result.getOutput().get(1));
    }

    @Test
    void testRenderDefaultColor() {
        MenuPrompt uut = new MenuPrompt();
        Output result = uut.render();

        assertEquals(2, result.getOutput().size());
        assertEquals("", result.getOutput().get(0));
        assertEquals("[green]Please [yellow]make your selection[green]: ", result.getOutput().get(1));
    }

    @Test
    void testRenderOneColor() {
        MenuPrompt uut = new MenuPrompt();
        Output result = uut.render(BLUE);

        assertEquals(2, result.getOutput().size());
        assertEquals("", result.getOutput().get(0));
        assertEquals("[green]Please [blue]make your selection[green]: ", result.getOutput().get(1));
    }
}
