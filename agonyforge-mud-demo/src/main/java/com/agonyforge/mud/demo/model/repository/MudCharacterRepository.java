package com.agonyforge.mud.demo.model.repository;

import com.agonyforge.mud.models.dynamodb.config.DynamoDbProperties;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.repository.AbstractRepository;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.agonyforge.mud.demo.model.impl.Constants.DB_PC;
import static com.agonyforge.mud.demo.model.impl.Constants.DB_ROOM;
import static com.agonyforge.mud.demo.model.impl.Constants.DB_USER;
import static com.agonyforge.mud.demo.model.impl.Constants.SORT_DATA;
import static com.agonyforge.mud.demo.model.impl.Constants.SORT_INSTANCE;

@Repository
public class MudCharacterRepository extends AbstractRepository<MudCharacter> {
    public static final Logger LOGGER = LoggerFactory.getLogger(MudCharacterRepository.class);

    @Autowired
    public MudCharacterRepository(DynamoDbClient dynamoDbClient,
                                  DynamoDbProperties.TableNames tableNames) {
        super(dynamoDbClient, tableNames, MudCharacter.class);
    }

    @Override
    public MudCharacter newInstance() {
        return new MudCharacter();
    }

    public Optional<MudCharacter> getById(UUID id, boolean prototype) {
        Map<String, Condition> filter = new HashMap<>();

        filter.put("pk", Condition.builder()
            .comparisonOperator(ComparisonOperator.EQ)
            .attributeValueList(AttributeValue.builder().s(DB_PC + id).build())
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
                LOGGER.warn("No players returned for {}", id);
                return Optional.empty();
            }

            MudCharacter item = newInstance();
            List<Map<String, AttributeValue>> items = response.items();

            item.thaw(items.get(0));

            return Optional.of(item);
        } catch (DynamoDbException e) {
            LOGGER.error("DynamoDbException: {}", e.getMessage(), e);
        }

        return Optional.empty();
    }

    public List<MudCharacter> getByUser(String user) {
        Map<String, AttributeValue> lastKeyEvaluated = null;
        List<MudCharacter> results = new ArrayList<>();

        do {
            Map<String, Condition> filter = new HashMap<>();

            filter.put("gsi2pk", Condition.builder()
                .comparisonOperator(ComparisonOperator.EQ)
                .attributeValueList(AttributeValue.builder().s(DB_USER + user).build())
                .build());

            QueryRequest request = QueryRequest.builder()
                .tableName(tableNames.getTableName())
                .indexName(tableNames.getGsi2())
                .keyConditions(filter)
                .exclusiveStartKey(lastKeyEvaluated)
                .build();

            try {
                QueryResponse response = dynamoDbClient.query(request);

                results.addAll(response.items()
                    .stream()
                    .map(item -> {
                        MudCharacter ch = newInstance();
                        ch.thaw(item);
                        return ch;
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

    public List<MudCharacter> getByRoom(Long roomId) {
        Map<String, AttributeValue> lastKeyEvaluated = null;
        List<MudCharacter> results = new ArrayList<>();

        do {
            Map<String, Condition> filter = new HashMap<>();

            filter.put("gsi2pk", Condition.builder()
                .comparisonOperator(ComparisonOperator.EQ)
                .attributeValueList(AttributeValue.builder().s(DB_ROOM + roomId).build())
                .build());

            filter.put("pk", Condition.builder()
                .comparisonOperator(ComparisonOperator.BEGINS_WITH)
                .attributeValueList(AttributeValue.builder().s(DB_PC).build())
                .build());

            QueryRequest request = QueryRequest.builder()
                .tableName(tableNames.getTableName())
                .indexName(tableNames.getGsi2())
                .keyConditions(filter)
                .exclusiveStartKey(lastKeyEvaluated)
                .build();

            try {
                QueryResponse response = dynamoDbClient.query(request);

                results.addAll(response.items()
                    .stream()
                    .map(item -> {
                        MudCharacter ch = newInstance();
                        ch.thaw(item);
                        return ch;
                    })
                    .toList());

                lastKeyEvaluated = response.lastEvaluatedKey();
            } catch (DynamoDbException e) {
                LOGGER.error("DynamoDbException: {}", e.getMessage(), e);
                lastKeyEvaluated = null;
            }
        } while(lastKeyEvaluated != null && lastKeyEvaluated.size() > 0);

        return results;
    }
}
