package com.agonyforge.mud.demo.model.constant;

import com.agonyforge.mud.demo.model.util.BaseEnumSetConverter;
import com.agonyforge.mud.demo.model.util.PersistentEnum;

public enum PlayerFlag implements PersistentEnum {
    HOLYLIGHT("can see everything");

    private final String description;

    PlayerFlag(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static class Converter extends BaseEnumSetConverter<PlayerFlag> {
        public Converter() {
            super(PlayerFlag.class);
        }
    }
}
