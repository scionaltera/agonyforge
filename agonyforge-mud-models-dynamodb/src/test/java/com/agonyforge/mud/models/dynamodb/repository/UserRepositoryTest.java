package com.agonyforge.mud.models.dynamodb.repository;

import com.agonyforge.mud.models.dynamodb.DynamoDbInitializer;
import com.agonyforge.mud.models.dynamodb.config.DynamoDbProperties;
import com.agonyforge.mud.models.dynamodb.impl.User;
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
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserRepositoryTest {
    @Mock
    private DynamoDbProperties.TableNames tableNames;

    private static DynamoDbClient dynamoDbClient;

    private static DynamoDBProxyServer server;

    @BeforeAll
    static void setUp() throws Exception {
        dynamoDbClient = DynamoDbClient
            .builder()
            .endpointOverride(new URI("http://localhost:8000"))
            .region(Region.US_WEST_2)
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create("x", "x")))
            .build();

        server = ServerRunner.createServerFromCommandLineArgs(new String[] { "-inMemory", "-port", "8000" });
        server.start();

        // create tables and stuff
        new DynamoDbInitializer(dynamoDbClient).initialize();
    }

    @AfterAll
    static void tearDown() throws Exception {
        server.stop();
    }

    @Test
    void testGetByPrincipal() {
        when(tableNames.getTableName()).thenReturn("agonyforge");

        UserRepository uut = new UserRepository(dynamoDbClient, tableNames);
        User user = new User();

        user.setPrincipalName("principal");
        user.setGivenName("Given");
        user.setEmailAddress("e@mail.address");

        uut.save(user);

        Optional<User> resultOptional = uut.getByPrincipal("principal");
        User result = resultOptional.orElseThrow();

        assertEquals(user.getPrincipalName(), result.getPrincipalName());
        assertEquals(user.getGivenName(), result.getGivenName());
        assertEquals(user.getEmailAddress(), result.getEmailAddress());
    }

    @Test
    void testGetByPrincipalNotFound() {
        when(tableNames.getTableName()).thenReturn("agonyforge");

        UserRepository uut = new UserRepository(dynamoDbClient, tableNames);
        Optional<User> resultOptional = uut.getByPrincipal("noSuchUser");
        assertThrows(NoSuchElementException.class, resultOptional::orElseThrow);
    }
}
