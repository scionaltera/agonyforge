package com.agonyforge.mud.demo.model.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class BaseEnumSetConverterTest {
    @Test
    void testConvertToDatabaseColumn() {
        EnumSet<TestEnum> test = EnumSet.of(TestEnum.ABLE, TestEnum.CHARLIE, TestEnum.EASY);
        TestEnum.Converter uut = new TestEnum.Converter();

        Long result = uut.convertToDatabaseColumn(test);

        assertEquals(21L, result);
    }

    @Test
    void testConvertToDatabaseColumnNull() {
        TestEnum.Converter uut = new TestEnum.Converter();

        Long result = uut.convertToDatabaseColumn(null);

        assertEquals(0L, result);
    }


    @Test
    void testConvertToEntityAttribute() {
        Long test = 21L;
        TestEnum.Converter uut = new TestEnum.Converter();

        EnumSet<TestEnum> result = uut.convertToEntityAttribute(test);

        assertTrue(result.containsAll(List.of(TestEnum.ABLE, TestEnum.CHARLIE, TestEnum.EASY)));
    }

    @Test
    void testConvertToEntityAttributeNull() {
        TestEnum.Converter uut = new TestEnum.Converter();

        EnumSet<TestEnum> result = uut.convertToEntityAttribute(null);

        assertEquals(EnumSet.noneOf(TestEnum.class), result);
    }

    public enum TestEnum implements PersistentEnum {
        ABLE,
        BAKER,
        CHARLIE,
        DOG,
        EASY,
        FOX;

        public static class Converter extends BaseEnumSetConverter<TestEnum> {
            public Converter() {
                super(TestEnum.class);
            }
        }
    }
}
