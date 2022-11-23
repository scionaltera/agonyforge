package com.agonyforge.mud.models.dynamodb.repository;

import com.agonyforge.mud.models.dynamodb.DynamoDbInitializer;
import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.ServerSocket;
import java.net.URI;

@ExtendWith(MockitoExtension.class)
public abstract class DynamoDbLocalInitializingTest {
    protected static DynamoDbClient dynamoDbClient;
    protected static DynamoDBProxyServer server;

    @BeforeAll
    static void setUp() throws Exception {
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
    static void tearDown() throws Exception {
        server.stop();
    }
}
