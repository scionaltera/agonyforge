package com.agonyforge.mud.demo.cli.menu;

import com.agonyforge.mud.core.cli.Color;
import com.agonyforge.mud.core.web.model.Output;
import org.junit.jupiter.api.Test;
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
public class MenuItemTest {
    @Test
    void testGetItem() {
        String foo = "foo";
        MenuItem uut = new MenuItem("T", "Test", foo);

        assertEquals(foo, uut.getItem());
    }

    @ParameterizedTest
    @MethodSource
    void testRender(Color[] colors, String expected) {
        MenuItem uut = new MenuItem("T", "Test");
        Output result = uut.render(colors);

        assertEquals(1, result.getOutput().size());
        assertEquals(expected, result.getOutput().get(0));
    }

    private static Stream<Arguments> testRender() {
        return Stream.of(
            Arguments.of(new Color[] {BLUE, RED}, "[blue]T[red]) [blue]Test"),
            Arguments.of(new Color[] {BLUE}, "[blue]T[green]) [blue]Test"),
            Arguments.of(new Color[] {}, "[yellow]T[green]) [yellow]Test")
        );
    }
}
