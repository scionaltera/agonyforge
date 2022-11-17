package com.agonyforge.mud.demo.cli.menu;

import com.agonyforge.mud.core.cli.Color;
import com.agonyforge.mud.core.web.model.Output;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static com.agonyforge.mud.core.cli.Color.BLUE;
import static com.agonyforge.mud.core.cli.Color.RED;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class DemoMenuTitleTest {
    @ParameterizedTest
    @MethodSource
    void testRender(Color[] colors, String expected) {
        DemoMenuTitle uut = new DemoMenuTitle("Test");
        Output result = uut.render(colors);
        String dividerText = (colors.length > 1 ? "[red]" : "[green]") + "*".repeat(10);

        assertEquals(4, result.getOutput().size());
        assertEquals("", result.getOutput().get(0));
        assertEquals(dividerText, result.getOutput().get(1));
        assertEquals(expected, result.getOutput().get(2));
        assertEquals(dividerText, result.getOutput().get(3));
    }

    private static Stream<Arguments> testRender() {
        return Stream.of(
            Arguments.of(new Color[] {BLUE, RED}, "[red]* [blue] Test [red] *"),
            Arguments.of(new Color[] {BLUE}, "[green]* [blue] Test [green] *"),
            Arguments.of(new Color[] {}, "[green]* [yellow] Test [green] *")
        );
    }
}
