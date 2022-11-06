package com.agonyforge.mud.demo.cli.menu;

import com.agonyforge.mud.core.web.model.Output;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.agonyforge.mud.core.cli.menu.Color.BLUE;
import static com.agonyforge.mud.core.cli.menu.Color.RED;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class DemoMenuPromptTest {
    @Test
    void testRender() {
        DemoMenuPrompt uut = new DemoMenuPrompt();
        Output result = uut.render(BLUE, RED);

        assertEquals(1, result.getOutput().size());
        assertEquals("[blue]Please [red]make your selection[blue]: ", result.getOutput().get(0));
    }

    @Test
    void testRenderDefaultColor() {
        DemoMenuPrompt uut = new DemoMenuPrompt();
        Output result = uut.render();

        assertEquals(1, result.getOutput().size());
        assertEquals("[yellow]Please [green]make your selection[yellow]: ", result.getOutput().get(0));
    }

    @Test
    void testRenderOneColor() {
        DemoMenuPrompt uut = new DemoMenuPrompt();
        Output result = uut.render(BLUE);

        assertEquals(1, result.getOutput().size());
        assertEquals("[blue]Please [green]make your selection[blue]: ", result.getOutput().get(0));
    }
}
