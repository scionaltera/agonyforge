package com.agonyforge.mud.core.cli;

import com.agonyforge.mud.core.web.model.Output;

import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads a text file from the classpath into an Output object.
 */
public class OutputLoader {
    public OutputLoader() {
        throw new UnsupportedOperationException("This class may not be instantiated.");
    }

    public static Output loadTextFile(String filename) throws IOException {
        InputStream is = new ClassPathResource(filename).getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        List<String> lines = new ArrayList<>();
        Output output = new Output();

        while (reader.ready()) {
            lines.add(reader.readLine().replaceAll("\\s", "&nbsp;"));
        }

        output.append(lines);

        return output;
    }
}
