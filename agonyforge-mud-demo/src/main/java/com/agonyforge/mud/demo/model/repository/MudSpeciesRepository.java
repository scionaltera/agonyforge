package com.agonyforge.mud.demo.model.repository;

import com.agonyforge.mud.demo.model.impl.MudSpecies;
import com.agonyforge.mud.models.dynamodb.config.DynamoDbProperties;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.agonyforge.mud.demo.model.impl.ModelConstants.DB_SPECIES;
import static com.agonyforge.mud.demo.model.impl.ModelConstants.SORT_DATA;

@Repository
public class MudSpeciesRepository extends AbstractRepository<MudSpecies> {
    public static final Logger LOGGER = LoggerFactory.getLogger(MudSpeciesRepository.class);

    @Autowired
    public MudSpeciesRepository(DynamoDbClient dynamoDbClient,
                                DynamoDbProperties.TableNames tableNames) {
        super(dynamoDbClient, tableNames, MudSpecies.class);
    }

    @Override
    public MudSpecies newInstance() {
        return new MudSpecies();
    }

    public Optional<MudSpecies> getById(UUID id) {
        Map<String, Condition> filter = new HashMap<>();

        filter.put("pk", Condition.builder()
            .comparisonOperator(ComparisonOperator.EQ)
            .attributeValueList(AttributeValue.builder().s(DB_SPECIES + id).build())
            .build());

        filter.put("sk", Condition.builder()
            .comparisonOperator(ComparisonOperator.EQ)
            .attributeValueList(AttributeValue.builder().s(SORT_DATA).build())
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

            MudSpecies item = newInstance();
            List<Map<String, AttributeValue>> items = response.items();

            item.thaw(items.get(0));

            return Optional.of(item);
        } catch (DynamoDbException e) {
            LOGGER.error("DynamoDbException: {}", e.getMessage(), e);
        }

        return Optional.empty();
    }
}
