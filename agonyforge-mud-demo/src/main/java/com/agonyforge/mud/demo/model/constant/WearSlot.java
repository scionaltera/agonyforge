package com.agonyforge.mud.demo.model.constant;

import com.agonyforge.mud.demo.model.util.BaseEnumSetConverter;
import com.agonyforge.mud.demo.model.util.PersistentEnum;

public enum WearSlot implements PersistentEnum {
    FINGER_LEFT("left finger", "worn on left finger"),
    FINGER_RIGHT("right finger", "worn on right finger"),
    NECK("neck", "worn on neck"),
    BODY("body", "worn on body"),
    HEAD("head", "worn on head"),
    LEGS("legs", "worn on legs"),
    FEET("feet", "worn on feet"),
    HANDS("hands", "worn on hands"),
    ARMS("arms", "worn on arms"),
    WAIST("waist", "worn on waist"),
    WRIST_LEFT("left wrist", "worn on left wrist"),
    WRIST_RIGHT("right wrist", "worn on right wrist"),
    EARS("ears", "worn on ears"),
    EYES("eyes", "worn on eyes"),
    FACE("face", "worn on face"),
    ANKLE_LEFT("left ankle", "worn on left ankle"),
    ANKLE_RIGHT("right ankle", "worn on right ankle"),
    HELD_OFF("off hand", "held in off hand"),
    HELD_MAIN("main hand", "held in main hand");

    private final String name;
    private final String phrase;

    WearSlot(String name, String phrase) {
        this.name = name;
        this.phrase = phrase;
    }

    public String getName() {
        return name;
    }

    public String getPhrase() {
        return phrase;
    }

    public static class Converter extends BaseEnumSetConverter<WearSlot> {
        public Converter() {
            super(WearSlot.class);
        }
    }
}
