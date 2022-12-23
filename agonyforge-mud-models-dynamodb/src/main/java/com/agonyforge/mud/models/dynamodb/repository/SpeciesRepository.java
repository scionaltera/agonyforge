package com.agonyforge.mud.models.dynamodb.repository;

import com.agonyforge.mud.models.dynamodb.config.DynamoDbProperties;
import com.agonyforge.mud.models.dynamodb.impl.Species;
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

import static com.agonyforge.mud.models.dynamodb.impl.Constants.DB_SPECIES;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.TYPE_SPECIES;

@Repository
public class SpeciesRepository extends AbstractRepository<Species> {
    public static final Logger LOGGER = LoggerFactory.getLogger(SpeciesRepository.class);

    @Autowired
    public SpeciesRepository(DynamoDbClient dynamoDbClient,
                             DynamoDbProperties.TableNames tableNames) {
        super(dynamoDbClient, tableNames, Species.class);
    }

    @Override
    public Species newInstance() {
        return new Species();
    }

    public Optional<Species> getById(UUID id) {
        Map<String, Condition> filter = new HashMap<>();

        filter.put("pk", Condition.builder()
            .comparisonOperator(ComparisonOperator.EQ)
            .attributeValueList(AttributeValue.builder().s(DB_SPECIES + id).build())
            .build());

        QueryRequest request = QueryRequest.builder()
            .tableName(tableNames.getTableName())
            .keyConditions(filter)
            .build();

        try {
            QueryResponse response = dynamoDbClient.query(request);

            if (!response.hasItems() || response.items().size() <= 0) {
                LOGGER.warn("No species returned for {}", id);
                return Optional.empty();
            }

            Species item = newInstance();
            List<Map<String, AttributeValue>> items = response.items();

            item.thaw(items.get(0));

            return Optional.of(item);
        } catch (DynamoDbException e) {
            LOGGER.error("DynamoDbException: {}", e.getMessage(), e);
        }

        return Optional.empty();
    }

    public List<Species> getAll() {
        Map<String, Condition> filter = new HashMap<>();

        filter.put("gsi1pk", Condition.builder()
            .comparisonOperator(ComparisonOperator.EQ)
            .attributeValueList(AttributeValue.builder().s(TYPE_SPECIES).build())
            .build());

        QueryRequest request = QueryRequest.builder()
            .tableName(tableNames.getTableName())
            .indexName(tableNames.getGsi1())
            .keyConditions(filter)
            .build();

        try {
            QueryResponse response = dynamoDbClient.query(request);

            return response.items()
                .stream()
                .map(item -> {
                    Species s = newInstance();
                    s.thaw(item);
                    return s;
                })
                .collect(Collectors.toList());
        } catch (DynamoDbException e) {
            LOGGER.error("DynamoDbException: {}", e.getMessage(), e);
        }

        return List.of();
    }
}
