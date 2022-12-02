package com.agonyforge.mud.models.dynamodb.constant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class DirectionTest {
    @Test
    void test() {
        assertEquals("north", Direction.NORTH.getName());
        assertEquals("south", Direction.NORTH.getOpposite());
    }
}
