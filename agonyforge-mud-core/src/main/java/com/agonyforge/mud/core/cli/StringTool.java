package com.agonyforge.mud.core.cli;

import com.agonyforge.mud.core.web.model.Output;

public class StringTool {
    public static final int WORD_WRAP = 80;

    public static Output softWrap(String in, int length) {
        return softWrap(in, length, Color.DEFAULT);
    }

    public static Output softWrap(String in, int length, Color color) {
        Output output = new Output();
        int lastPos = 0;

        if (in == null) {
            return output;
        }

        for (int i = 0; i < in.length(); i++) {
            if (i - lastPos > length && in.charAt(i) == ' ') {
                output.append("%s%s", color.toString(), in.substring(lastPos, i).trim());
                lastPos = i;
            }
        }

        output.append("%s%s", color.toString(), in.substring(lastPos).trim());

        return output;
    }
}
