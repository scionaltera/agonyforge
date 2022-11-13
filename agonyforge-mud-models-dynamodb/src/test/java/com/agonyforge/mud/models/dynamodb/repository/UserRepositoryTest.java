package com.agonyforge.mud.models.dynamodb.repository;

import com.agonyforge.mud.models.dynamodb.DynamoDbInitializer;
import com.agonyforge.mud.models.dynamodb.impl.User;
import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import java.net.URI;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SystemStubsExtension.class)
public class UserRepositoryTest {

    private static DynamoDbClient dynamoDbClient;

    private static DynamoDBProxyServer server;

    @SuppressWarnings("unused")
    @SystemStub
    private static final EnvironmentVariables environmentVariables = new EnvironmentVariables(
        "DYNAMO_TABLE_NAME", "agonyforge",
        "DYNAMO_GSI1_NAME", "gsi1",
        "DYNAMO_GSI2_NAME", "gsi2"
    );

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
        UserRepository uut = new UserRepository(dynamoDbClient);
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
        UserRepository uut = new UserRepository(dynamoDbClient);
        Optional<User> resultOptional = uut.getByPrincipal("noSuchUser");
        assertThrows(NoSuchElementException.class, resultOptional::orElseThrow);
    }
}
