package com.agonyforge.mud.demo.model.repository;

import com.agonyforge.mud.models.dynamodb.config.DynamoDbProperties;
import com.agonyforge.mud.demo.model.impl.CommandReference;
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

import static com.agonyforge.mud.demo.model.impl.ModelConstants.TYPE_COMMAND;

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
        Map<String, AttributeValue> lastKeyEvaluated = null;
        List<CommandReference> results = new ArrayList<>();

        do {
            Map<String, Condition> filter = new HashMap<>();

            filter.put("gsi1pk", Condition.builder()
                .comparisonOperator(ComparisonOperator.EQ)
                .attributeValueList(AttributeValue.builder().s(TYPE_COMMAND).build())
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
                    .map(attributes -> {
                        CommandReference item = newInstance();
                        item.thaw(attributes);
                        return item;
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
}
