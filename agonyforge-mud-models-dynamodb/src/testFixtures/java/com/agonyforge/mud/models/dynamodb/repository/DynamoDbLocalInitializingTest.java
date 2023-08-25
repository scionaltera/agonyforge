package com.agonyforge.mud.models.dynamodb.repository;

import com.agonyforge.mud.models.dynamodb.config.DynamoDbProperties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@ExtendWith(MockitoExtension.class)
public abstract class DynamoDbLocalInitializingTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamoDbLocalInitializingTest.class);

    protected static DynamoDbClient dynamoDbClient;
    protected static final GenericContainer dynamoDbLocal;

    static {
        DockerImageName imageName = DockerImageName.parse("amazon/dynamodb-local:latest");
        dynamoDbLocal = new GenericContainer(imageName)
            .withExposedPorts(8000);
        dynamoDbLocal.start();
    }

    @Mock
    protected DynamoDbProperties.TableNames tableNames;

    @BeforeAll
    static void setUpClient() throws Exception {
        LOGGER.info("Initializing DynamoDB client");
        dynamoDbClient = DynamoDbClient
            .builder()
            .endpointOverride(new URI(String.format("http://%s:%s",
                dynamoDbLocal.getHost(),
                dynamoDbLocal.getFirstMappedPort())))
            .region(Region.US_WEST_2)
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create("x", "x")))
            .build();
    }

    @BeforeEach
    void setUp() {
        Mockito.lenient().when(tableNames.getTableName()).thenReturn("agonyforge");
        Mockito.lenient().when(tableNames.getGsi1()).thenReturn("gsi1");
        Mockito.lenient().when(tableNames.getGsi2()).thenReturn("gsi2");
    }

    protected static DynamoDbClient getDynamoDbClient() {
        return dynamoDbClient;
    }
}
