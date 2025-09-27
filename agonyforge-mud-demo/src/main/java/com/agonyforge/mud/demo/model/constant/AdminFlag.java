package com.agonyforge.mud.demo.model.constant;

import com.agonyforge.mud.demo.model.util.BaseEnumSetConverter;
import com.agonyforge.mud.demo.model.util.PersistentEnum;

public enum AdminFlag implements PersistentEnum {
    HOLYLIGHT("can see everything"),
    PEACEFUL("cannot be attacked");

    private final String description;

    AdminFlag(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static class Converter extends BaseEnumSetConverter<AdminFlag> {
        public Converter() {
            super(AdminFlag.class);
        }
    }
}
