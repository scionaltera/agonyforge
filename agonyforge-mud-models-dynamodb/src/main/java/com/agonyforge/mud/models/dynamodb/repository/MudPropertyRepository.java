package com.agonyforge.mud.models.dynamodb.repository;

import com.agonyforge.mud.models.dynamodb.config.DynamoDbProperties;
import com.agonyforge.mud.models.dynamodb.impl.MudProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.agonyforge.mud.models.dynamodb.impl.Constants.DB_PROPERTY;

@Repository
public class MudPropertyRepository extends AbstractRepository<MudProperty> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MudPropertyRepository.class);

    @Autowired
    public MudPropertyRepository(DynamoDbClient dynamoDbClient,
                                 DynamoDbProperties.TableNames tableNames) {
        super(dynamoDbClient, tableNames, MudProperty.class);
    }

    @Override
    public MudProperty newInstance() {
        return new MudProperty();
    }

    public Optional<MudProperty> getByName(String name) {
        Map<String, Condition> filter = new HashMap<>();

        filter.put("pk", Condition.builder()
            .comparisonOperator(ComparisonOperator.EQ)
            .attributeValueList(AttributeValue.builder().s(DB_PROPERTY).build())
            .build());

        filter.put("sk", Condition.builder()
            .comparisonOperator(ComparisonOperator.EQ)
            .attributeValueList(AttributeValue.builder().s(name).build())
            .build());

        QueryRequest request = QueryRequest.builder()
            .tableName(tableNames.getTableName())
            .keyConditions(filter)
            .build();

        try {
            QueryResponse response = dynamoDbClient.query(request);

            if (!response.hasItems() || response.items().size() <= 0) {
                LOGGER.warn("No items returned for {}", name);
                return Optional.empty();
            }

            MudProperty item = newInstance();
            List<Map<String, AttributeValue>> items = response.items();

            item.thaw(items.get(0));

            return Optional.of(item);
        } catch (DynamoDbException e) {
            LOGGER.error("DynamoDbException: {}", e.getMessage(), e);
        }

        return Optional.empty();
    }
}
