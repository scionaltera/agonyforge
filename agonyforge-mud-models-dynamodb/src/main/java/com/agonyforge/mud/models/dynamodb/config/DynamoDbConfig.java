package com.agonyforge.mud.models.dynamodb.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

import java.net.URI;

@Configuration
public class DynamoDbConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamoDbConfig.class);

    @Bean
    public DynamoDbClient dynamoDbClient() {
        String dynamoEndpoint = System.getenv("DYNAMO_ENDPOINT");
        String dynamoRegion = System.getenv("DYNAMO_REGION");

        DynamoDbClientBuilder builder = DynamoDbClient.builder();

        if (dynamoEndpoint != null && !dynamoEndpoint.isBlank()) {
            LOGGER.info("Setting endpoint override for DynamoDB: {}", dynamoEndpoint);
            builder.endpointOverride(URI.create(dynamoEndpoint));
        }

        if (dynamoRegion != null && !dynamoRegion.isBlank()) {
            LOGGER.debug("Setting region for DynamoDB: {}", dynamoRegion);
            builder.region(Region.of(dynamoRegion));
        }

        return builder.build();
    }
}
