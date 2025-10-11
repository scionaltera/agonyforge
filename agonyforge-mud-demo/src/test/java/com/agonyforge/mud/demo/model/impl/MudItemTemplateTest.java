package com.agonyforge.mud.demo.model.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MudItemTemplateTest {
    @Test
    void testBuildInstance() {
        // This test must call MudItemTemplate.buildInstance() and validate that the resulting MudItem's fields are populated correctly.
        MudItemTemplate template = new MudItemTemplate();
        
        // Test that buildInstance() creates a proper instance
        MudItem instance = template.buildInstance();
        assertNotNull(instance);
        assertNotNull(instance.getTemplate());
        assertEquals(template, instance.getTemplate());
    }

    @Test
    void testId() {
        MudItemTemplate template = new MudItemTemplate();
        Long id = 1L;
        template.setId(id);
        assertEquals(id, template.getId());
    }

    @Test
    void testEquals() {
        MudItemTemplate template1 = new MudItemTemplate();
        template1.setId(1L);
        
        MudItemTemplate template2 = new MudItemTemplate();
        template2.setId(1L);
        
        MudItemTemplate template3 = new MudItemTemplate();
        template3.setId(2L);

        assertEquals(template2, template1);
        assertNotEquals(template3, template1);
        assertNotEquals(null, template1);
        assertNotEquals(new Object(), template1);
    }

    @Test
    void testHashCode() {
        MudItemTemplate template1 = new MudItemTemplate();
        template1.setId(1L);
        
        MudItemTemplate template2 = new MudItemTemplate();
        template2.setId(1L);
        
        assertEquals(template1.hashCode(), template2.hashCode());
    }
}
