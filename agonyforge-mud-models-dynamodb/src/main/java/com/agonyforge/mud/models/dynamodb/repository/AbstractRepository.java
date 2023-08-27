package com.agonyforge.mud.models.dynamodb.repository;

import com.agonyforge.mud.models.dynamodb.Persistent;
import com.agonyforge.mud.models.dynamodb.config.DynamoDbProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ComparisonOperator;
import software.amazon.awssdk.services.dynamodb.model.Condition;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.agonyforge.mud.models.dynamodb.impl.DynamoDbConstants.ISO_8601;
import static com.agonyforge.mud.models.dynamodb.impl.DynamoDbConstants.KEY_MODIFIED;

public abstract class AbstractRepository<T extends Persistent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRepository.class);

    protected final DynamoDbClient dynamoDbClient;
    protected final DynamoDbProperties.TableNames tableNames;
    protected final Class<T> klass;

    public AbstractRepository(DynamoDbClient dynamoDbClient,
                              DynamoDbProperties.TableNames tableNames,
                              Class<T> klass) {
        this.dynamoDbClient = dynamoDbClient;
        this.tableNames = tableNames;
        this.klass = klass;
    }

    public abstract T newInstance();

    public List<T> getByType(String type) {
        Map<String, AttributeValue> lastKeyEvaluated = null;
        List<T> results = new ArrayList<>();

        do {
            Map<String, Condition> filter = new HashMap<>();

            filter.put("gsi1pk", Condition.builder()
                .comparisonOperator(ComparisonOperator.EQ)
                .attributeValueList(AttributeValue.builder().s(type).build())
                .build());

            QueryRequest request = QueryRequest.builder()
                .tableName(tableNames.getTableName())
                .indexName(tableNames.getGsi1())
                .keyConditions(filter)
                .exclusiveStartKey(lastKeyEvaluated)
                .build();

            try {
                QueryResponse response = dynamoDbClient.query(request);

                results.addAll(response.items()
                    .stream()
                    .map(item -> {
                        T thing = newInstance();
                        thing.thaw(item);
                        return thing;
                    })
                    .toList());

                lastKeyEvaluated = response.lastEvaluatedKey();
            } catch (DynamoDbException e) {
                LOGGER.error("DynamoDbException: {}", e.getMessage(), e);
                lastKeyEvaluated = null;
            }
        } while (lastKeyEvaluated != null && lastKeyEvaluated.size() > 0);

        return results;
    }

    public void saveAll(List<T> items) {
        for (int i = 0; i < items.size(); i += 25) {
            List<T> sublist = items.subList(i, Math.min(i + 25, items.size()));
            List<WriteRequest> writeRequests = sublist
                .stream()
                .map(item -> WriteRequest
                    .builder()
                    .putRequest(PutRequest
                        .builder()
                        .item(enrichItem(item.freeze()))
                        .build())
                    .build())
                .collect(Collectors.toList());
            Map<String, List<WriteRequest>> operations = new HashMap<>();

            operations.put(tableNames.getTableName(), writeRequests);

            BatchWriteItemRequest batch = BatchWriteItemRequest
                .builder()
                .requestItems(operations)
                .build();

            try {
                dynamoDbClient.batchWriteItem(batch);
                LOGGER.info("Wrote batch of {} items", sublist.size());
            } catch (DynamoDbException e) {
                LOGGER.error("DynamoDbException: {}", e.getMessage(), e);
            }
        }
    }

    public void save(T item) {
        Map<String, AttributeValue> map = enrichItem(item.freeze());
        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableNames.getTableName())
            .item(map)
            .build();

        try {
            dynamoDbClient.putItem(request);
        } catch (DynamoDbException e) {
            LOGGER.error("DynamoDbException: {}", e.getMessage(), e);
        }
    }

    public void delete(T item) {
        Map<String, AttributeValue> map = enrichItem(item.freeze());
        DeleteItemRequest request = DeleteItemRequest
            .builder()
            .tableName(tableNames.getTableName())
            .key(Map.of(
                "pk", map.get("pk"),
                "sk", map.get("sk")))
            .build();

        try {
            dynamoDbClient.deleteItem(request);
        } catch (DynamoDbException e) {
            LOGGER.error("DynamoDbException: {}", e.getMessage(), e);
        }
    }

    private Map<String, AttributeValue> enrichItem(Map<String, AttributeValue> item) {
        item.put(KEY_MODIFIED, AttributeValue.builder().s(ISO_8601.format(new Date())).build());

        return item;
    }
}
