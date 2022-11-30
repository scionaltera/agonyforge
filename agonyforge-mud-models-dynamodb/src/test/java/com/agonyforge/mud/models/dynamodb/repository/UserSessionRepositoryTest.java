package com.agonyforge.mud.models.dynamodb.repository;

import com.agonyforge.mud.models.dynamodb.impl.UserSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class UserSessionRepositoryTest extends DynamoDbLocalInitializingTest {
    @Test
    void testGetByPrincipal() {
        UserSessionRepository uut = new UserSessionRepository(dynamoDbClient, tableNames);
        UserSession session = new UserSession();
        String principal = UUID.randomUUID().toString();

        session.setPrincipalName(principal);
        session.setRemoteIpAddress("999.888.777.666");

        uut.save(session);

        List<UserSession> results = uut.getByPrincipal(principal);

        assertEquals(1, results.size());

        UserSession result = results.get(0);

        assertEquals(session.getPrincipalName(), result.getPrincipalName());
        assertEquals(session.getRemoteIpAddress(), result.getRemoteIpAddress());
    }

    @Test
    void testGetByPrincipalNotFound() {
        UserSessionRepository uut = new UserSessionRepository(dynamoDbClient, tableNames);
        List<UserSession> results = uut.getByPrincipal("noSuchUser");
        assertEquals(0, results.size());
    }
}
