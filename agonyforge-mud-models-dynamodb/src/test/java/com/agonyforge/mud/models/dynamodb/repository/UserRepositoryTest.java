package com.agonyforge.mud.models.dynamodb.repository;

import com.agonyforge.mud.models.dynamodb.config.DynamoDbProperties;
import com.agonyforge.mud.models.dynamodb.impl.User;;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserRepositoryTest extends DynamoDbLocalInitializingTest {
    @Mock
    private DynamoDbProperties.TableNames tableNames;

    @Test
    void testGetByPrincipal() {
        when(tableNames.getTableName()).thenReturn("agonyforge");

        UserRepository uut = new UserRepository(dynamoDbClient, tableNames);
        User user = new User();

        user.setPrincipalName("principal");
        user.setGivenName("Given");
        user.setEmailAddress("e@mail.address");

        uut.save(user);

        Optional<User> resultOptional = uut.getByPrincipal("principal");
        User result = resultOptional.orElseThrow();

        assertEquals(user.getPrincipalName(), result.getPrincipalName());
        assertEquals(user.getGivenName(), result.getGivenName());
        assertEquals(user.getEmailAddress(), result.getEmailAddress());
    }

    @Test
    void testGetByPrincipalNotFound() {
        when(tableNames.getTableName()).thenReturn("agonyforge");

        UserRepository uut = new UserRepository(dynamoDbClient, tableNames);
        Optional<User> resultOptional = uut.getByPrincipal("noSuchUser");
        assertThrows(NoSuchElementException.class, resultOptional::orElseThrow);
    }
}
