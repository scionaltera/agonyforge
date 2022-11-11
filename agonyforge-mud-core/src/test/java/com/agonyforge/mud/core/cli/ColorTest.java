package com.agonyforge.mud.core.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.agonyforge.mud.core.cli.Color.BLUE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ColorTest {
    @Test
    void testToString() {
        assertEquals("[blue]", BLUE.toString());
    }
}
