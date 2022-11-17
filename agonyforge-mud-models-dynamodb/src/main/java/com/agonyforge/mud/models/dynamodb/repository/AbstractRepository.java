package com.agonyforge.mud.models.dynamodb.repository;

import com.agonyforge.mud.models.dynamodb.Persistent;
import com.agonyforge.mud.models.dynamodb.config.DynamoDbProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutRequest;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.agonyforge.mud.models.dynamodb.impl.Constants.ISO_8601;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.KEY_MODIFIED;

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
