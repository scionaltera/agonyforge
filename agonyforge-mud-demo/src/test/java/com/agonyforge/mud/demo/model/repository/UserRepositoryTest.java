package com.agonyforge.mud.demo.model.repository;

import com.agonyforge.mud.demo.model.DynamoDbInitializer;
import com.agonyforge.mud.demo.model.impl.User;
import com.agonyforge.mud.models.dynamodb.repository.DynamoDbLocalInitializingTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserRepositoryTest extends DynamoDbLocalInitializingTest {
    @BeforeAll
    static void setUpDatabase() throws Exception {
        new DynamoDbInitializer(getDynamoDbClient()).initialize();
    }

    @Test
    void testGetByPrincipal() {
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
        UserRepository uut = new UserRepository(dynamoDbClient, tableNames);
        Optional<User> resultOptional = uut.getByPrincipal("noSuchUser");
        assertThrows(NoSuchElementException.class, resultOptional::orElseThrow);
    }
}
