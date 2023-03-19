package com.agonyforge.mud.models.dynamodb.repository;

import com.agonyforge.mud.models.dynamodb.impl.CommandReference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class CommandRepositoryTest extends DynamoDbLocalInitializingTest {
    @Test
    void testGetByPriority() {
        CommandRepository uut = new CommandRepository(dynamoDbClient, tableNames);
        CommandReference ref1 = new CommandReference();
        CommandReference ref2 = new CommandReference();

        ref1.setPriority("100");
        ref1.setName("SOUTH");
        ref1.setBeanName("southCommand");

        ref2.setPriority("1000");
        ref2.setName("SAY");
        ref2.setBeanName("sayCommand");

        uut.save(ref1);
        uut.save(ref2);

        List<CommandReference> refs = uut.getByPriority();
        CommandReference result1 = refs.get(0);
        CommandReference result2 = refs.get(1);

        assertEquals(ref1.getPriority(), result1.getPriority());
        assertEquals(ref1.getName(), result1.getName());
        assertEquals(ref1.getBeanName(), result1.getBeanName());

        assertEquals(ref2.getPriority(), result2.getPriority());
        assertEquals(ref2.getName(), result2.getName());
        assertEquals(ref2.getBeanName(), result2.getBeanName());
    }
}
