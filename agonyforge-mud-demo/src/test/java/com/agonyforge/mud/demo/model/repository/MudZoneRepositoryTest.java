package com.agonyforge.mud.demo.model.repository;

import com.agonyforge.mud.demo.model.DynamoDbInitializer;
import com.agonyforge.mud.demo.model.impl.MudZone;
import com.agonyforge.mud.models.dynamodb.repository.DynamoDbLocalInitializingTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class MudZoneRepositoryTest extends DynamoDbLocalInitializingTest {
    @BeforeAll
    static void setUpDatabase() throws Exception {
        new DynamoDbInitializer(getDynamoDbClient()).initialize();
    }

    @Test
    void testGetById() {
        MudZoneRepository uut = new MudZoneRepository(dynamoDbClient, tableNames);
        MudZone zone = new MudZone();

        zone.setId(1L);
        zone.setName("Test Zone");

        uut.save(zone);

        Optional<MudZone> resultOptional = uut.getById(1L);
        MudZone result = resultOptional.orElseThrow();

        assertEquals(zone.getId(), result.getId());
        assertEquals(zone.getName(), result.getName());
    }

    @Test
    void testGetByIdNotFound() {
        MudZoneRepository uut = new MudZoneRepository(dynamoDbClient, tableNames);
        Optional<MudZone> resultOptional = uut.getById(1L);

        assertTrue(resultOptional.isEmpty());
    }
}
