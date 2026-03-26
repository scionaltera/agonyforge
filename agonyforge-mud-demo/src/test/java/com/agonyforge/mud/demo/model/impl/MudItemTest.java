package com.agonyforge.mud.demo.model.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MudItemTest {
    @Mock
    private MudItemTemplate mockTemplate;

    private MudItem mudItem;

    @BeforeEach
    void setUp() {
        mudItem = new MudItem();
    }

    @Test
    void testGetId() {
        Long expectedId = 1L;
        mudItem.setId(expectedId);
        assertEquals(expectedId, mudItem.getId());
    }

    @Test
    void testSetId() {
        Long expectedId = 2L;
        mudItem.setId(expectedId);
        assertEquals(expectedId, mudItem.getId());
    }

    @Test
    void testGetTemplate() {
        mudItem.setTemplate(mockTemplate);
        assertEquals(mockTemplate, mudItem.getTemplate());
    }

    @Test
    void testSetTemplate() {
        mudItem.setTemplate(mockTemplate);
        assertEquals(mockTemplate, mudItem.getTemplate());
    }

    @Test
    void testEquals() {
        MudItem item1 = new MudItem();
        item1.setId(1L);

        MudItem item2 = new MudItem();
        item2.setId(1L);

        assertEquals(item1, item2);
    }

    @Test
    void testEqualsNull() {
        assertFalse(mudItem.equals(null));
    }

    @Test
    void testEqualsDifferentClass() {
        assertFalse(mudItem.equals("not a mud item"));
    }

    @Test
    void testEqualsDifferentId() {
        MudItem item1 = new MudItem();
        item1.setId(1L);

        MudItem item2 = new MudItem();
        item2.setId(2L);

        assertNotEquals(item1, item2);
    }

    @Test
    void testHashCode() {
        MudItem item = new MudItem();
        item.setId(1L);
        assertEquals(item.hashCode(), item.hashCode());
    }

    @Test
    void testHashCodeNullId() {
        assertEquals(0, mudItem.hashCode());
    }
}
