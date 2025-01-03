package com.agonyforge.mud.demo.model.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ItemComponentTest {
    private static final Random RANDOM = new Random();

    @Test
    void testId() {
        Long id = RANDOM.nextLong();
        ItemComponent uut = new ItemComponent();

        uut.setId(id);

        assertEquals(id, uut.getId());
    }

    @Test
    void testNameList() {
        Set<String> nameList = Set.of("able", "baker", "charlie");
        ItemComponent uut = new ItemComponent();

        uut.setNameList(nameList);

        assertEquals(nameList, uut.getNameList());
    }

    @Test
    void testShortDescription() {
        String shortDescription = "a wild test";
        ItemComponent uut = new ItemComponent();

        uut.setShortDescription(shortDescription);

        assertEquals(shortDescription, uut.getShortDescription());
    }

    @Test
    void testLongDescription() {
        String longDescription = "A wild test is zipping around the room.";
        ItemComponent uut = new ItemComponent();

        uut.setLongDescription(longDescription);

        assertEquals(longDescription, uut.getLongDescription());
    }
}
