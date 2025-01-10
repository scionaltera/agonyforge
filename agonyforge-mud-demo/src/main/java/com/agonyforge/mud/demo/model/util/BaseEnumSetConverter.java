package com.agonyforge.mud.demo.model.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.EnumSet;

@Converter
public abstract class BaseEnumSetConverter <E extends Enum<E> & PersistentEnum> implements AttributeConverter<EnumSet<E>, Long> {
    private final Class<E> klass;

    public BaseEnumSetConverter(Class<E> klass) {
        this.klass = klass;
    }

    @Override
    public Long convertToDatabaseColumn(EnumSet<E> attribute) {
        long total = 0;

        if (attribute == null) {
            return total;
        }

        for (E constant : attribute) {
            total |= 1L << constant.ordinal();
        }

        return total;
    }

    @Override
    public EnumSet<E> convertToEntityAttribute(Long dbData) {
        EnumSet<E> results = EnumSet.noneOf(klass);

        if (dbData == null) {
            return results;
        }

        for (E constant : klass.getEnumConstants()) {
            if ((dbData & (1L << constant.ordinal())) != 0) {
                results.add(constant);
            }
        }

        return results;
    }
}
