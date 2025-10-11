package com.agonyforge.mud.demo.model.impl;

import com.agonyforge.mud.demo.model.constant.WearMode;
import com.agonyforge.mud.demo.model.constant.WearSlot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MudItemTemplateTest {
    @Test
    void testBuildInstance() {
        // This test must call MudItemTemplate.buildInstance() and validate that the resulting MudItem's fields are populated correctly.
        MudItemTemplate template = new MudItemTemplate();
        
        // Initialize the item component in the template to avoid null pointer
        ItemComponent itemComponent = new ItemComponent();
        itemComponent.setNameList(Collections.singleton("test item"));
        itemComponent.setShortDescription("A test item");
        itemComponent.setLongDescription("This is a long description of a test item");
        itemComponent.setWearSlots(EnumSet.of(WearSlot.HEAD));
        itemComponent.setWearMode(WearMode.WEARABLE); // This will be converted to WearMode enum
        
        // Set the item component on the template
        template.setItem(itemComponent);
        
        // Test that buildInstance() creates a proper instance
        MudItem instance = template.buildInstance();
        assertNotNull(instance);
        assertNotNull(instance.getTemplate());
        assertEquals(template, instance.getTemplate());
        
        // Verify that the fields were copied correctly
        assertNotNull(instance.getItem());
        assertTrue(instance.getItem().getNameList().contains("test item"));
        assertEquals("A test item", instance.getItem().getShortDescription());
        assertEquals("This is a long description of a test item", instance.getItem().getLongDescription());
        assertEquals(EnumSet.of(WearSlot.HEAD), instance.getItem().getWearSlots());
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

@ExtendWith(MockitoExtension.class)
public class MudItemTemplateTest {
    @Test
    void testBuildInstance() {
        // This test must call MudItemTemplate.buildInstance() and validate that the resulting MudItem's fields are populated correctly.
        MudItemTemplate template = new MudItemTemplate();
        
        // Initialize the item component in the template to avoid null pointer
        ItemComponent itemComponent = new ItemComponent();
        itemComponent.setNameList(Collections.singleton("test item"));
        itemComponent.setShortDescription("A test item");
        itemComponent.setLongDescription("This is a long description of a test item");
        itemComponent.setWearSlots(EnumSet.of(WearSlot.HEAD));
        itemComponent.setWearMode(WearMode.WEARABLE); // This will be converted to WearMode enum
        
        // Set the item component on the template
        template.setItem(itemComponent);
        
        // Test that buildInstance() creates a proper instance
        MudItem instance = template.buildInstance();
        assertNotNull(instance);
        assertNotNull(instance.getTemplate());
        assertEquals(template, instance.getTemplate());
        
        // Verify that the fields were copied correctly
        assertNotNull(instance.getItem());
        assertTrue(instance.getItem().getNameList().contains("test item"));
        assertEquals("A test item", instance.getItem().getShortDescription());
        assertEquals("This is a long description of a test item", instance.getItem().getLongDescription());
        assertEquals(EnumSet.of(WearSlot.HEAD), instance.getItem().getWearSlots());
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
