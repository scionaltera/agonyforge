package com.agonyforge.mud.models.dynamodb.repository;

import com.agonyforge.mud.models.dynamodb.DynamoDbInitializer;
import com.agonyforge.mud.models.dynamodb.impl.UserSession;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SystemStubsExtension.class)
public class UserSessionRepositoryTest {

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
        UserSessionRepository uut = new UserSessionRepository(dynamoDbClient);
        UserSession session = new UserSession();

        session.setPrincipalName("principal");
        session.setRemoteIpAddress("999.888.777.666");

        uut.save(session);

        List<UserSession> results = uut.getByPrincipal("principal");

        assertEquals(1, results.size());

        UserSession result = results.get(0);

        assertEquals(session.getPrincipalName(), result.getPrincipalName());
        assertEquals(session.getRemoteIpAddress(), result.getRemoteIpAddress());
    }

    @Test
    void testGetByPrincipalNotFound() {
        UserSessionRepository uut = new UserSessionRepository(dynamoDbClient);
        List<UserSession> results = uut.getByPrincipal("noSuchUser");
        assertEquals(0, results.size());
    }
}
