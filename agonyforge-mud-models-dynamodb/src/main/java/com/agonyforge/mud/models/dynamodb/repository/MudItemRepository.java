package com.agonyforge.mud.models.dynamodb.repository;

import com.agonyforge.mud.models.dynamodb.config.DynamoDbProperties;
import com.agonyforge.mud.models.dynamodb.impl.MudItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ComparisonOperator;
import software.amazon.awssdk.services.dynamodb.model.Condition;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.agonyforge.mud.models.dynamodb.impl.Constants.DB_ITEM;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.DB_PC;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.DB_ROOM;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.SORT_DATA;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.SORT_INSTANCE;

@Repository
public class MudItemRepository extends AbstractRepository<MudItem> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MudItemRepository.class);

    @Autowired
    public MudItemRepository(DynamoDbClient dynamoDbClient,
                             DynamoDbProperties.TableNames tableNames) {
        super(dynamoDbClient, tableNames, MudItem.class);
    }

    @Override
    public MudItem newInstance() {
        return new MudItem();
    }

    public Optional<MudItem> getById(UUID id, boolean prototype) {
        Map<String, Condition> filter = new HashMap<>();

        filter.put("pk", Condition.builder()
            .comparisonOperator(ComparisonOperator.EQ)
            .attributeValueList(AttributeValue.builder().s(DB_ITEM + id).build())
            .build());

        if (prototype) {
            filter.put("sk", Condition.builder()
                .comparisonOperator(ComparisonOperator.EQ)
                .attributeValueList(AttributeValue.builder().s(SORT_DATA).build())
                .build());
        } else {
            filter.put("sk", Condition.builder()
                .comparisonOperator(ComparisonOperator.EQ)
                .attributeValueList(AttributeValue.builder().s(SORT_INSTANCE).build())
                .build());
        }

        QueryRequest request = QueryRequest.builder()
            .tableName(tableNames.getTableName())
            .keyConditions(filter)
            .build();

        try {
            QueryResponse response = dynamoDbClient.query(request);

            if (!response.hasItems() || response.items().size() <= 0) {
                LOGGER.warn("No items returned for {}", id);
                return Optional.empty();
            }

            MudItem item = newInstance();
            List<Map<String, AttributeValue>> items = response.items();

            item.thaw(items.get(0));

            return Optional.of(item);
        } catch (DynamoDbException e) {
            LOGGER.error("DynamoDbException: {}", e.getMessage(), e);
        }

        return Optional.empty();
    }

    public List<MudItem> getByCharacter(String character) {
        Map<String, Condition> filter = new HashMap<>();

        filter.put("gsi2pk", Condition.builder()
            .comparisonOperator(ComparisonOperator.EQ)
            .attributeValueList(AttributeValue.builder().s(DB_PC + character).build())
            .build());

        filter.put("pk", Condition.builder()
            .comparisonOperator(ComparisonOperator.BEGINS_WITH)
            .attributeValueList(AttributeValue.builder().s(DB_ITEM).build())
            .build());

        QueryRequest request = QueryRequest.builder()
            .tableName(tableNames.getTableName())
            .indexName(tableNames.getGsi2())
            .keyConditions(filter)
            .build();

        try {
            QueryResponse response = dynamoDbClient.query(request);

            return response.items()
                .stream()
                .map(item -> {
                    MudItem mudItem = newInstance();
                    mudItem.thaw(item);
                    return mudItem;
                })
                .collect(Collectors.toList());
        } catch (DynamoDbException e) {
            LOGGER.error("DynamoDbException: {}", e.getMessage(), e);
        }

        return List.of();
    }

    public List<MudItem> getByRoom(Long roomId) {
        Map<String, Condition> filter = new HashMap<>();

        filter.put("gsi2pk", Condition.builder()
            .comparisonOperator(ComparisonOperator.EQ)
            .attributeValueList(AttributeValue.builder().s(DB_ROOM + roomId).build())
            .build());
        filter.put("pk", Condition.builder()
            .comparisonOperator(ComparisonOperator.BEGINS_WITH)
            .attributeValueList(AttributeValue.builder().s(DB_ITEM).build())
            .build());

        QueryRequest request = QueryRequest.builder()
            .tableName(tableNames.getTableName())
            .indexName(tableNames.getGsi2())
            .keyConditions(filter)
            .build();

        try {
            QueryResponse response = dynamoDbClient.query(request);

            return response.items()
                .stream()
                .map(item -> {
                    MudItem mudItem = newInstance();
                    mudItem.thaw(item);
                    return mudItem;
                })
                .collect(Collectors.toList());
        } catch (DynamoDbException e) {
            LOGGER.error("DynamoDbException: {}", e.getMessage(), e);
        }

        return List.of();
    }
}
