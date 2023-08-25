package com.agonyforge.mud.demo.model.repository;

import com.agonyforge.mud.demo.model.DynamoDbInitializer;
import com.agonyforge.mud.demo.model.impl.MudProperty;
import com.agonyforge.mud.models.dynamodb.repository.DynamoDbLocalInitializingTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MudPropertyRepositoryTest extends DynamoDbLocalInitializingTest {
    @BeforeAll
    static void setUpDatabase() throws Exception {
        new DynamoDbInitializer(getDynamoDbClient()).initialize();
    }

    @Test
    void testGetByName() {
        MudPropertyRepository uut = new MudPropertyRepository(dynamoDbClient, tableNames);
        MudProperty item = new MudProperty();
        String name = "test.property";
        String value = UUID.randomUUID().toString();

        item.setName(name);
        item.setValue(value);

        uut.save(item);

        Optional<MudProperty> resultOptional = uut.getByName(name);
        MudProperty result = resultOptional.orElseThrow();

        assertEquals(item.getName(), result.getName());
        assertEquals(item.getValue(), result.getValue());
    }
}
