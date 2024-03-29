package com.agonyforge.mud.core.cli.menu.impl;

import com.agonyforge.mud.core.cli.Color;
import com.agonyforge.mud.core.cli.menu.impl.MenuDivider;
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
public class MenuDividerTest {
    @ParameterizedTest
    @MethodSource
    void testRender(Color[] colors, String expected) {
        MenuDivider uut = new MenuDivider(10);
        Output result = uut.render(colors);

        assertEquals(1, result.getOutput().size());
        assertEquals(expected, result.getOutput().get(0));
    }

    private static Stream<Arguments> testRender() {
        return Stream.of(
            Arguments.of(new Color[] {BLUE, RED}, "[red]**********"),
            Arguments.of(new Color[] {BLUE}, "[green]**********"),
            Arguments.of(new Color[] {}, "[green]**********")
        );
    }
}
