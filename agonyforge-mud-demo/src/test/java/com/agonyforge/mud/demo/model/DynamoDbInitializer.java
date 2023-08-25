package com.agonyforge.mud.demo.model;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.CreateTableResponse;
import software.amazon.awssdk.services.dynamodb.model.GlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.model.InternalServerErrorException;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.LimitExceededException;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughputExceededException;
import software.amazon.awssdk.services.dynamodb.model.RequestLimitExceededException;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

import java.util.ArrayList;
import java.util.List;

public class DynamoDbInitializer {
    private final DynamoDbClient dynamoDB;

    public DynamoDbInitializer(DynamoDbClient dynamoDbClient) {
        this.dynamoDB = dynamoDbClient;
    }

    public void initialize() {
        try {
            // Create CreateTableRequest
            CreateTableRequest createTableRequest = createTableInput();
            CreateTableResponse createTableResult = dynamoDB.createTable(createTableRequest);
            System.out.println("Successfully created table.");

        } catch (Exception e) {
            handleCreateTableErrors(e);
        }
    }

    private static CreateTableRequest createTableInput() {
        // Create KeySchema
        List<KeySchemaElement> keySchema = new ArrayList<>();
        keySchema.add(KeySchemaElement.builder().attributeName("pk").keyType(KeyType.HASH).build());
        keySchema.add(KeySchemaElement.builder().attributeName("sk").keyType(KeyType.RANGE).build());

        // Create AttributeDefinitions
        List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
        attributeDefinitions.add(AttributeDefinition.builder().attributeName("pk").attributeType(ScalarAttributeType.S).build());
        attributeDefinitions.add(AttributeDefinition.builder().attributeName("sk").attributeType(ScalarAttributeType.S).build());
        attributeDefinitions.add(AttributeDefinition.builder().attributeName("gsi1pk").attributeType(ScalarAttributeType.S).build());
        attributeDefinitions.add(AttributeDefinition.builder().attributeName("gsi2pk").attributeType(ScalarAttributeType.S).build());

        // Create GlobalSecondaryIndexes
        List<GlobalSecondaryIndex> globalSecondaryIndexes = new ArrayList<>();

        // Create KeySchema
        List<KeySchemaElement> gsi1KeySchema = new ArrayList<>();
        gsi1KeySchema.add(KeySchemaElement.builder().attributeName("gsi1pk").keyType(KeyType.HASH).build());
        gsi1KeySchema.add(KeySchemaElement.builder().attributeName("pk").keyType(KeyType.RANGE).build());

        List<KeySchemaElement> gsi2KeySchema = new ArrayList<>();
        gsi2KeySchema.add(KeySchemaElement.builder().attributeName("gsi2pk").keyType(KeyType.HASH).build());
        gsi2KeySchema.add(KeySchemaElement.builder().attributeName("pk").keyType(KeyType.RANGE).build());

        // Create ProvisionedThroughput
        ProvisionedThroughput gsi1provisionedThroughput = ProvisionedThroughput.builder().readCapacityUnits(1L).writeCapacityUnits(1L).build();
        ProvisionedThroughput gsi2provisionedThroughput = ProvisionedThroughput.builder().readCapacityUnits(1L).writeCapacityUnits(1L).build();

        GlobalSecondaryIndex gsi1 = GlobalSecondaryIndex.builder()
            .indexName("gsi1")
            .keySchema(gsi1KeySchema)
            .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
            .provisionedThroughput(gsi1provisionedThroughput)
            .build();

        GlobalSecondaryIndex gsi2 = GlobalSecondaryIndex.builder()
            .indexName("gsi2")
            .keySchema(gsi2KeySchema)
            .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
            .provisionedThroughput(gsi2provisionedThroughput)
            .build();

        globalSecondaryIndexes.add(gsi1);
        globalSecondaryIndexes.add(gsi2);

        // Create ProvisionedThroughput
        ProvisionedThroughput provisionedThroughput = ProvisionedThroughput.builder().readCapacityUnits(1L).writeCapacityUnits(1L).build();

        return CreateTableRequest.builder()
            .tableName("agonyforge")
            .keySchema(keySchema)
            .attributeDefinitions(attributeDefinitions)
            .provisionedThroughput(provisionedThroughput)
            .globalSecondaryIndexes(globalSecondaryIndexes)
            .build();
    }


    // Handles errors during CreateTable execution. Use recommendations in error messages below to add error handling specific to
    // your application use-case.
    private static void handleCreateTableErrors(Exception exception) {
        try {
            throw exception;
        } catch (ResourceInUseException riue) {
            System.out.println("Request rejected because it tried to delete a table currently in the CREATING state, " +
                "retry again after a while. Error: " + riue.getMessage());
        } catch (LimitExceededException lee) {
            System.out.println("Request rejected because it has exceeded the allowed simultaneous table operations i.e, 50" +
                "These operations include CreateTable, UpdateTable, DeleteTable,UpdateTimeToLive, RestoreTableFromBackup, and RestoreTableToPointInTime. Error: " + lee.getMessage());
        } catch (Exception e) {
            System.out.println("Unknown exception: " + e.getMessage());
        }
    }


    private static void handleCommonErrors(Exception exception) {
        try {
            throw exception;
        } catch (InternalServerErrorException isee) {
            System.out.println("Internal Server Error, generally safe to retry with exponential back-off. Error: " + isee.getMessage());
        } catch (RequestLimitExceededException rlee) {
            System.out.println("Throughput exceeds the current throughput limit for your account, increase account level throughput before " +
                "retrying. Error: " + rlee.getMessage());
        } catch (ProvisionedThroughputExceededException ptee) {
            System.out.println("Request rate is too high. If you're using a custom retry strategy make sure to retry with exponential back-off. " +
                "Otherwise consider reducing frequency of requests or increasing provisioned capacity for your table or secondary index. Error: " +
                ptee.getMessage());
        } catch (ResourceNotFoundException rnfe) {
            System.out.println("One of the tables was not found, verify table exists before retrying. Error: " + rnfe.getMessage());
        } catch (Exception e) {
            System.out.println("An exception occurred, investigate and configure retry strategy. Error: " + e.getMessage());
        }
    }
}
