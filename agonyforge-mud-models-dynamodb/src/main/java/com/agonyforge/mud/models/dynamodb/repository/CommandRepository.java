package com.agonyforge.mud.models.dynamodb.repository;

import com.agonyforge.mud.models.dynamodb.config.DynamoDbProperties;
import com.agonyforge.mud.models.dynamodb.impl.CommandReference;
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
import java.util.stream.Collectors;

import static com.agonyforge.mud.models.dynamodb.impl.Constants.TYPE_COMMAND;

@Repository
public class CommandRepository extends AbstractRepository<CommandReference> {
    public static final Logger LOGGER = LoggerFactory.getLogger(CommandRepository.class);

    @Autowired
    public CommandRepository(DynamoDbClient dynamoDbClient, DynamoDbProperties.TableNames tableNames) {
        super(dynamoDbClient, tableNames, CommandReference.class);
    }

    @Override
    public CommandReference newInstance() {
        return new CommandReference();
    }

    public List<CommandReference> getByPriority() {
        Map<String, Condition> filter = new HashMap<>();

        filter.put("gsi1pk", Condition.builder()
            .comparisonOperator(ComparisonOperator.EQ)
            .attributeValueList(AttributeValue.builder().s(TYPE_COMMAND).build())
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
                .map(attributes -> {
                    CommandReference item = newInstance();
                    item.thaw(attributes);
                    return item;
                })
                .collect(Collectors.toList());
        } catch (DynamoDbException e) {
            LOGGER.error("DynamoDbException: {}", e.getMessage(), e);
        }

        return List.of();
    }
}
