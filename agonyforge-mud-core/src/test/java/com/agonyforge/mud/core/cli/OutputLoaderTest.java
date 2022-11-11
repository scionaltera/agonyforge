package com.agonyforge.mud.core.cli;

import com.agonyforge.mud.core.web.model.Output;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class OutputLoaderTest {
    @Test
    void testLoadTextFile() throws IOException {
        Output result = OutputLoader.loadTextFile("output-loader-test.txt");

        assertEquals(5, result.getOutput().size());
        assertEquals("one", result.getOutput().get(0));
        assertEquals("two", result.getOutput().get(1));
        assertEquals("three", result.getOutput().get(2));
        assertEquals("four", result.getOutput().get(3));
        assertEquals("five", result.getOutput().get(4));
    }

    @Test
    void testInstantiateClass() {
        assertThrows(UnsupportedOperationException.class, OutputLoader::new);
    }
}
