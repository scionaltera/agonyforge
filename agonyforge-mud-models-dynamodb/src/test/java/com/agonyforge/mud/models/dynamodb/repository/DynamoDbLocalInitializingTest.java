package com.agonyforge.mud.models.dynamodb.repository;

import com.agonyforge.mud.models.dynamodb.DynamoDbInitializer;
import com.agonyforge.mud.models.dynamodb.config.DynamoDbProperties;
import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.ServerSocket;
import java.net.URI;

import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public abstract class DynamoDbLocalInitializingTest {
    protected static DynamoDbClient dynamoDbClient;
    protected static DynamoDBProxyServer server;

    @Mock
    protected DynamoDbProperties.TableNames tableNames;

    @BeforeAll
    static void setUpAll() throws Exception {
        String port;

        try (ServerSocket serverSocket = new ServerSocket(0)) {
            port = String.valueOf(serverSocket.getLocalPort());
        }

        dynamoDbClient = DynamoDbClient
            .builder()
            .endpointOverride(new URI("http://localhost:" + port))
            .region(Region.US_WEST_2)
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create("x", "x")))
            .build();

        server = ServerRunner.createServerFromCommandLineArgs(new String[] { "-inMemory", "-port", port });
        server.start();

        // create tables and stuff
        new DynamoDbInitializer(dynamoDbClient).initialize();
    }

    @AfterAll
    static void tearDownAll() throws Exception {
        server.stop();
    }

    @BeforeEach
    void setUp() {
        lenient().when(tableNames.getTableName()).thenReturn("agonyforge");
        lenient().when(tableNames.getGsi1()).thenReturn("gsi1");
        lenient().when(tableNames.getGsi2()).thenReturn("gsi2");
    }
}
