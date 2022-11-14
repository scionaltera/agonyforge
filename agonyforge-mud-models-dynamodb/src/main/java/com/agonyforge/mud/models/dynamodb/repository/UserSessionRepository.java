package com.agonyforge.mud.models.dynamodb.repository;

import com.agonyforge.mud.models.dynamodb.impl.UserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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

import static com.agonyforge.mud.models.dynamodb.impl.Constants.DB_USER;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.DYNAMO_TABLE_NAME;

@Component
public class UserSessionRepository extends AbstractRepository<UserSession> {
    public static final Logger LOGGER = LoggerFactory.getLogger(UserSessionRepository.class);

    @Autowired
    public UserSessionRepository(DynamoDbClient dynamoDbClient) {
        super(dynamoDbClient, UserSession.class);
    }

    @Override
    public UserSession newInstance() {
        return new UserSession();
    }

    public List<UserSession> getByPrincipal(String principalName) {
        Map<String, Condition> filter = new HashMap<>();

        filter.put("pk", Condition.builder()
            .comparisonOperator(ComparisonOperator.EQ)
            .attributeValueList(AttributeValue.builder().s(DB_USER + principalName).build())
            .build());

        QueryRequest request = QueryRequest.builder()
            .tableName(DYNAMO_TABLE_NAME)
            .keyConditions(filter)
            .build();

        try {
            QueryResponse response = dynamoDbClient.query(request);

            return response.items()
                .stream()
                .map(item -> {
                    UserSession session = newInstance();
                    session.thaw(item);
                    return session;
                })
                .collect(Collectors.toList());
        } catch (DynamoDbException e) {
            LOGGER.error("DynamoDbException: {}", e.getMessage(), e);
        }

        return List.of();
    }
}
