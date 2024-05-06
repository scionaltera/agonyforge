package com.agonyforge.mud.core.web.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Output {
    private static final Pattern MULTIPLE_SPACES = Pattern.compile("(\\s(\\[.+?])?\\s+)");

    private final List<String> lines = new ArrayList<>();

    public Output() {
        // this method intentionally left blank
    }

    public Output(String output, Object ... args) {
        append(output, args);
    }

    public Output(Collection<String> output) {
        append(output);
    }

    public Output(Output... outputs) {
        append(outputs);
    }

    public Output append(String output, Object ... args) {
        lines.add(nonBreakingSpaces(String.format(output, args)));

        return this;
    }

    public Output append(Collection<String> output) {
        lines.addAll(output);

        return this;
    }

    public Output append(Output... outputs) {
        Arrays.stream(outputs).forEach(output -> this.lines.addAll(output.lines));

        return this;
    }


    public List<String> getOutput() {
        return toList();
    }

    public List<String> toList() {
        return new ArrayList<>(lines);
    }

    @Override
    public String toString() {
        return String.join("\n", lines);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Output)) return false;
        Output output = (Output) o;
        return lines.equals(output.lines);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lines);
    }

    private String nonBreakingSpaces(String line) {
        boolean done = false;

        while (!done) {
            Matcher m = MULTIPLE_SPACES.matcher(line);

            if (!m.find()) {
                done = true;
                continue;
            }

            String group1 = m.group(1);
            String group2 = m.group(2) == null ? "" : m.group(2);

            String replacement = String.format("&nbsp;%s%s",
                group2,
                "&nbsp;".repeat(group1.length() - group2.length() - 1));

            line = m.replaceFirst(replacement);
        }

        return line;
    }
}
