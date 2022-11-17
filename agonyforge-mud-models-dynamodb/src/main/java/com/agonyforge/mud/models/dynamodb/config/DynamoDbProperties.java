package com.agonyforge.mud.models.dynamodb.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

import java.net.URI;

@Configuration
@ConfigurationProperties(prefix = "mud.dynamo")
public class DynamoDbProperties {
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamoDbProperties.class);

    private String endpoint;
    private String region;
    private String tableName;
    private String gsi1Name;
    private String gsi2Name;

    @Bean
    public DynamoDbClient dynamoDbClient() {
        DynamoDbClientBuilder builder = DynamoDbClient.builder();

        if (endpoint != null && !endpoint.isBlank()) {
            LOGGER.info("Setting endpoint override for DynamoDB: {}", endpoint);
            builder.endpointOverride(URI.create(endpoint));
        }

        if (region != null && !region.isBlank()) {
            LOGGER.debug("Setting region for DynamoDB: {}", region);
            builder.region(Region.of(region));
        }

        return builder.build();
    }

    @Bean
    public TableNames dynamoTables() {
        return new TableNames(
            tableName,
            gsi1Name,
            gsi2Name);
    }

    public static class TableNames {
        private final String tableName;
        private final String gsi1;
        private final String gsi2;

        TableNames(String tableName, String gsi1, String gsi2) {
            this.tableName = tableName;
            this.gsi1 = gsi1;
            this.gsi2 = gsi2;
        }

        public String getTableName() {
            return tableName;
        }

        public String getGsi1() {
            return gsi1;
        }

        public String getGsi2() {
            return gsi2;
        }
    }

    public void setEndpoint(String endpoint) {
        LOGGER.debug("DynamoDB endpoint: {}", endpoint);
        this.endpoint = endpoint;
    }

    public void setRegion(String region) {
        LOGGER.debug("DynamoDB region: {}", region);
        this.region = region;
    }

    public void setTableName(String tableName) {
        LOGGER.debug("DynamoDB table name: {}", tableName);
        this.tableName = tableName;
    }

    public void setGsi1Name(String gsi1Name) {
        LOGGER.debug("DynamoDB gsi1 name: {}", gsi1Name);
        this.gsi1Name = gsi1Name;
    }

    public void setGsi2Name(String gsi2Name) {
        LOGGER.debug("DynamoDB gsi2 name: {}", gsi2Name);
        this.gsi2Name = gsi2Name;
    }
}
