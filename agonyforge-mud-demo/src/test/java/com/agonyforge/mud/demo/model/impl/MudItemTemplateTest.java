package com.agonyforge.mud.demo.model.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MudItemTemplateTest {
    @Test
    void testBuildInstance() {
        // AI! Create a new instance and verify it can be built
        MudItemTemplate template = new MudItemTemplate();
        assertNotNull(template);
    }

    @Test
    void testId() {
        // AI! Test the ID getter and setter
        MudItemTemplate template = new MudItemTemplate();
        Long id = 1L;
        template.setId(id);
        assertEquals(id, template.getId());
    }

    @Test
    void testEquals() {
        // AI! Test equals and hashCode methods
        MudItemTemplate template1 = new MudItemTemplate();
        template1.setId(1L);
        
        MudItemTemplate template2 = new MudItemTemplate();
        template2.setId(1L);
        
        MudItemTemplate template3 = new MudItemTemplate();
        template3.setId(2L);
        
        assertEquals(template1, template2);
        assertNotEquals(template1, template3);
        assertNotEquals(template1, null);
        assertNotEquals(template1, new Object());
    }

    @Test
    void testHashCode() {
        // AI! Test that hashCode is consistent with equals
        MudItemTemplate template1 = new MudItemTemplate();
        template1.setId(1L);
        
        MudItemTemplate template2 = new MudItemTemplate();
        template2.setId(1L);
        
        assertEquals(template1.hashCode(), template2.hashCode());
    }
}
