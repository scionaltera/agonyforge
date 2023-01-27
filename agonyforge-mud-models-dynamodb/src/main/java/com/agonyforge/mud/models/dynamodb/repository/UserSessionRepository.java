package com.agonyforge.mud.models.dynamodb.repository;

import com.agonyforge.mud.models.dynamodb.config.DynamoDbProperties;
import com.agonyforge.mud.models.dynamodb.impl.UserSession;
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
import java.util.stream.Collectors;

import static com.agonyforge.mud.models.dynamodb.impl.Constants.DB_USER;

@Repository
public class UserSessionRepository extends AbstractRepository<UserSession> {
    public static final Logger LOGGER = LoggerFactory.getLogger(UserSessionRepository.class);

    @Autowired
    public UserSessionRepository(DynamoDbClient dynamoDbClient, DynamoDbProperties.TableNames tableNames) {
        super(dynamoDbClient, tableNames, UserSession.class);
    }

    @Override
    public UserSession newInstance() {
        return new UserSession();
    }

    public List<UserSession> getByPrincipal(String principalName) {
        Map<String, AttributeValue> lastKeyEvaluated = null;
        List<UserSession> results = new ArrayList<>();

        do {
            Map<String, Condition> filter = new HashMap<>();

            filter.put("pk", Condition.builder()
                .comparisonOperator(ComparisonOperator.EQ)
                .attributeValueList(AttributeValue.builder().s(DB_USER + principalName).build())
                .build());

            QueryRequest request = QueryRequest.builder()
                .tableName(tableNames.getTableName())
                .keyConditions(filter)
                .build();

            try {
                QueryResponse response = dynamoDbClient.query(request);

                results.addAll(response.items()
                    .stream()
                    .map(item -> {
                        UserSession session = newInstance();
                        session.thaw(item);
                        return session;
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
