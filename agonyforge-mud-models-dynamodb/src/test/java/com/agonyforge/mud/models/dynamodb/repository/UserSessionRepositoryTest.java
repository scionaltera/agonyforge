package com.agonyforge.mud.models.dynamodb.repository;

import com.agonyforge.mud.models.dynamodb.config.DynamoDbProperties;
import com.agonyforge.mud.models.dynamodb.impl.UserSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserSessionRepositoryTest extends DynamoDbLocalInitializingTest {

    @Mock
    private DynamoDbProperties.TableNames tableNames;

    @Test
    void testGetByPrincipal() {
        when(tableNames.getTableName()).thenReturn("agonyforge");

        UserSessionRepository uut = new UserSessionRepository(dynamoDbClient, tableNames);
        UserSession session = new UserSession();

        session.setPrincipalName("principal");
        session.setRemoteIpAddress("999.888.777.666");

        uut.save(session);

        List<UserSession> results = uut.getByPrincipal("principal");

        assertEquals(1, results.size());

        UserSession result = results.get(0);

        assertEquals(session.getPrincipalName(), result.getPrincipalName());
        assertEquals(session.getRemoteIpAddress(), result.getRemoteIpAddress());
    }

    @Test
    void testGetByPrincipalNotFound() {
        when(tableNames.getTableName()).thenReturn("agonyforge");

        UserSessionRepository uut = new UserSessionRepository(dynamoDbClient, tableNames);
        List<UserSession> results = uut.getByPrincipal("noSuchUser");
        assertEquals(0, results.size());
    }
}
