package com.agonyforge.mud.core.cli.menu.impl;

import com.agonyforge.mud.core.cli.Color;
import com.agonyforge.mud.core.cli.menu.impl.MenuTitle;
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
public class MenuTitleTest {
    @ParameterizedTest
    @MethodSource
    void testRender(Color[] colors, String expected) {
        MenuTitle uut = new MenuTitle("Test");
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
            Arguments.of(new Color[] {BLUE, RED}, "[red]*&nbsp;[blue]&nbsp;Test&nbsp;[red]&nbsp;*"),
            Arguments.of(new Color[] {BLUE}, "[green]*&nbsp;[blue]&nbsp;Test&nbsp;[green]&nbsp;*"),
            Arguments.of(new Color[] {}, "[green]*&nbsp;[yellow]&nbsp;Test&nbsp;[green]&nbsp;*")
        );
    }
}
