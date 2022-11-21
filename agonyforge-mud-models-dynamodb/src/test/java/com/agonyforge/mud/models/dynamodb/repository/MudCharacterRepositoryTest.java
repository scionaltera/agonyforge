package com.agonyforge.mud.models.dynamodb.repository;

import com.agonyforge.mud.models.dynamodb.DynamoDbInitializer;
import com.agonyforge.mud.models.dynamodb.config.DynamoDbProperties;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MudCharacterRepositoryTest {
    @Mock
    private DynamoDbProperties.TableNames tableNames;

    private static DynamoDbClient dynamoDbClient;

    private static DynamoDBProxyServer server;

    @BeforeAll
    static void setUp() throws Exception {
        dynamoDbClient = DynamoDbClient
            .builder()
            .endpointOverride(new URI("http://localhost:8010"))
            .region(Region.US_WEST_2)
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create("x", "x")))
            .build();

        server = ServerRunner.createServerFromCommandLineArgs(new String[] { "-inMemory", "-port", "8010" });
        server.start();

        // create tables and stuff
        new DynamoDbInitializer(dynamoDbClient).initialize();
    }

    @AfterAll
    static void tearDown() throws Exception {
        server.stop();
    }

    @Test
    void testGetByIdPrototype() {
        when(tableNames.getTableName()).thenReturn("agonyforge");

        MudCharacterRepository uut = new MudCharacterRepository(dynamoDbClient, tableNames);
        MudCharacter ch = new MudCharacter();
        UUID uuid = UUID.randomUUID();
        String user = UUID.randomUUID().toString();

        ch.setId(uuid);
        ch.setUser(user);
        ch.setName("Scion");

        uut.save(ch);

        Optional<MudCharacter> resultOptional = uut.getById(uuid, true);
        MudCharacter result = resultOptional.orElseThrow();

        assertEquals(ch.getId(), result.getId());
        assertEquals(ch.getUser(), result.getUser());
        assertEquals(ch.getName(), result.getName());
        assertEquals(ch.isPrototype(), result.isPrototype());
    }

    @Test
    void testGetByIdInstance() {
        when(tableNames.getTableName()).thenReturn("agonyforge");

        MudCharacterRepository uut = new MudCharacterRepository(dynamoDbClient, tableNames);
        MudCharacter ch = new MudCharacter();
        UUID uuid = UUID.randomUUID();
        String user = UUID.randomUUID().toString();

        ch.setId(uuid);
        ch.setUser(user);
        ch.setName("Scion");

        MudCharacter chInstance = ch.buildInstance();

        uut.saveAll(List.of(ch, chInstance));

        Optional<MudCharacter> resultOptional = uut.getById(uuid, false);
        MudCharacter result = resultOptional.orElseThrow();

        assertEquals(ch.getId(), result.getId());
        assertThrows(IllegalStateException.class, result::getUser);
        assertEquals(ch.getName(), result.getName());
        assertEquals(chInstance.isPrototype(), result.isPrototype());
    }

    @Test
    void testGetByIdEmpty() {
        when(tableNames.getTableName()).thenReturn("agonyforge");

        MudCharacterRepository uut = new MudCharacterRepository(dynamoDbClient, tableNames);
        UUID uuid = UUID.randomUUID();

        Optional<MudCharacter> resultOptional = uut.getById(uuid, true);
        assertTrue(resultOptional.isEmpty());
    }

    @Test
    void testGetByUser() {
        when(tableNames.getTableName()).thenReturn("agonyforge");
        when(tableNames.getGsi2()).thenReturn("gsi2");

        MudCharacterRepository uut = new MudCharacterRepository(dynamoDbClient, tableNames);
        MudCharacter ch = new MudCharacter();
        UUID uuid = UUID.randomUUID();
        String user = UUID.randomUUID().toString();

        ch.setId(uuid);
        ch.setUser(user);
        ch.setName("Scion");

        MudCharacter chInstance = ch.buildInstance();

        uut.saveAll(List.of(ch, chInstance));

        List<MudCharacter> results = uut.getByUser(user);
        MudCharacter result = results.get(0);

        assertEquals(1, results.size());

        assertEquals(ch.getId(), result.getId());
        assertEquals(ch.getUser(), result.getUser());
        assertEquals(ch.getName(), result.getName());
        assertEquals(ch.isPrototype(), result.isPrototype());
    }
}
