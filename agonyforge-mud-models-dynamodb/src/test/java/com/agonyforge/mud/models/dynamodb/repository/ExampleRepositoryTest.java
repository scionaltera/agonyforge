package com.agonyforge.mud.models.dynamodb.repository;

import com.agonyforge.mud.models.dynamodb.DynamoDbInitializer;
import com.agonyforge.mud.models.dynamodb.impl.Example;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ExampleRepositoryTest extends DynamoDbLocalInitializingTest {
    @BeforeAll
    static void setUpDatabase() throws Exception {
        new DynamoDbInitializer(getDynamoDbClient()).initialize();
    }

    @Test
    void testGetById() {
        ExampleRepository uut = new ExampleRepository(dynamoDbClient, tableNames);
        Example example = new Example();
        UUID uuid = UUID.randomUUID();

        example.setId(uuid);
        example.setFoo("bar");

        uut.save(example);

        Optional<Example> resultOptional = uut.getById(uuid);
        Optional<Example> emptyOptional = uut.getById(UUID.randomUUID());

        assertTrue(resultOptional.isPresent());
        assertFalse(emptyOptional.isPresent());

        Example result = resultOptional.get();

        assertEquals(result, example);
        assertEquals(example.getFoo(), result.getFoo());
    }
}
