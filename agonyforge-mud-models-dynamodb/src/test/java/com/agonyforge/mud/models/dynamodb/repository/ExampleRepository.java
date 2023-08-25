package com.agonyforge.mud.models.dynamodb.repository;

import com.agonyforge.mud.models.dynamodb.config.DynamoDbProperties;
import com.agonyforge.mud.models.dynamodb.impl.Example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ComparisonOperator;
import software.amazon.awssdk.services.dynamodb.model.Condition;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.agonyforge.mud.models.dynamodb.impl.Example.DB_EXAMPLE;

@Repository
public class ExampleRepository extends AbstractRepository<Example> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleRepository.class);
    @Autowired
    public ExampleRepository(DynamoDbClient dynamoDbClient,
                             DynamoDbProperties.TableNames tableNames) {
        super(dynamoDbClient, tableNames, Example.class);
    }

    @Override
    public Example newInstance() {
        return new Example();
    }

    public Optional<Example> getById(UUID id) {
        Map<String, Condition> filter = new HashMap<>();

        filter.put("pk", Condition.builder()
            .comparisonOperator(ComparisonOperator.EQ)
            .attributeValueList(AttributeValue.builder().s(DB_EXAMPLE + id).build())
            .build());

        QueryRequest request = QueryRequest.builder()
            .tableName(tableNames.getTableName())
            .keyConditions(filter)
            .build();

        try {
            QueryResponse response = dynamoDbClient.query(request);

            if (!response.hasItems() || response.items().size() <= 0) {
                LOGGER.warn("No examples returned for {}", id);
                return Optional.empty();
            }

            Example item = newInstance();
            List<Map<String, AttributeValue>> items = response.items();

            item.thaw(items.get(0));

            return Optional.of(item);
        } catch (DynamoDbException e) {
            LOGGER.error("DynamoDbException: {}", e.getMessage(), e);
        }

        return Optional.empty();
    }
}
