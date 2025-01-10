package com.agonyforge.mud.demo.model.constant;

import com.agonyforge.mud.demo.model.util.BaseEnumSetConverter;
import com.agonyforge.mud.demo.model.util.PersistentEnum;

public enum RoomFlag implements PersistentEnum {
    INDOORS("room is indoors");

    private final String description;

    RoomFlag(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static class Converter extends BaseEnumSetConverter<RoomFlag> {
        public Converter() {
            super(RoomFlag.class);
        }
    }
}
