package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.cli.question.CommandException;
import com.agonyforge.mud.demo.model.constant.WearSlot;
import com.agonyforge.mud.demo.model.impl.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AbstractCommandTest extends CommandTestBoilerplate {
    @Mock
    private MudCharacter ch, target;

    @Mock
    private CharacterComponent targetCharacterComponent;

    @Mock
    private MudItem sword, chair;

    @Mock
    private ItemComponent swordComponent, chairComponent;

    @Mock
    private LocationComponent chLocationComponent, targetLocationComponent, swordLocation;

    @Mock
    private MudRoom room;

    @Mock
    private Question question;

    @Mock
    private Binding commandBinding;

    private final Random random = new Random();

    @Test
    void testGetCharacterNotFound() {
        Long chId = random.nextLong();

        when(characterRepository.findById(eq(chId))).thenReturn(Optional.empty());
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));

        Output output = new Output();
        Command uut = new AbstractCommand(repositoryBundle, commService, applicationContext) {
            @Override
            public Question execute(Question question, WebSocketContext webSocketContext, List<Binding> bindings, Output output) {
                getCurrentCharacter(webSocketContext, output);
                return question;
            }
        };

        assertThrows(CommandException.class, () -> uut.execute(
            question,
            webSocketContext,
            List.of(commandBinding),
            output));
    }

    @Test
    void testGetCharacterInVoid() {
        Long chId = random.nextLong();

        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(null);
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));

        Output output = new Output();
        Command uut = new AbstractCommand(repositoryBundle, commService, applicationContext) {
            @Override
            public Question execute(Question question, WebSocketContext webSocketContext, List<Binding> bindings, Output output) {
                getCurrentCharacter(webSocketContext, output);
                return question;
            }
        };

        assertThrows(CommandException.class, () -> uut.execute(
            question,
            webSocketContext,
            List.of(commandBinding),
            output));
    }

    @Test
    void testGetCharacterValid() {
        Long chId = random.nextLong();

        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));

        Output output = new Output();
        Command uut = new AbstractCommand(repositoryBundle, commService, applicationContext) {
            @Override
            public Question execute(Question question, WebSocketContext webSocketContext, List<Binding> bindings, Output output) {
                getCurrentCharacter(webSocketContext, output);
                return question;
            }
        };

        assertEquals(question, uut.execute(
            question,
            webSocketContext,
            List.of(commandBinding),
            output));
    }

    @Test
    void findInventoryItem() {
        when(sword.getItem()).thenReturn(swordComponent);
        when(sword.getLocation()).thenReturn(swordLocation);
        when(swordLocation.getWorn()).thenReturn(EnumSet.noneOf(WearSlot.class));
        when(swordComponent.getNameList()).thenReturn(Set.of("sword"));
        when(itemRepository.findByLocationHeld(eq(ch))).thenReturn(List.of(sword));

        AbstractCommand uut = new AbstractCommand(repositoryBundle, commService, applicationContext) {
            @Override
            public Question execute(Question question, WebSocketContext webSocketContext, List<Binding> bindings, Output output) {
                return question;
            }
        };

        assertTrue(uut.findInventoryItem(ch, "sword").isPresent());
        assertTrue(uut.findInventoryItem(ch, "sw").isPresent());
    }

    @Test
    void findWornItem() {
        when(sword.getItem()).thenReturn(swordComponent);
        when(sword.getLocation()).thenReturn(swordLocation);
        when(swordLocation.getWorn()).thenReturn(EnumSet.of(WearSlot.HELD_MAIN));
        when(swordComponent.getNameList()).thenReturn(Set.of("sword"));
        when(itemRepository.findByLocationHeld(eq(ch))).thenReturn(List.of(sword));

        AbstractCommand uut = new AbstractCommand(repositoryBundle, commService, applicationContext) {
            @Override
            public Question execute(Question question, WebSocketContext webSocketContext, List<Binding> bindings, Output output) {
                return question;
            }
        };

        assertTrue(uut.findWornItem(ch, "sword").isPresent());
        assertTrue(uut.findWornItem(ch, "sw").isPresent());
        assertFalse(uut.findWornItem(ch, "chair").isPresent());
        assertFalse(uut.findWornItem(ch, "ch").isPresent());
    }

    @Test
    void findRoomItem() {
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);
        when(sword.getItem()).thenReturn(swordComponent);
        when(swordComponent.getNameList()).thenReturn(Set.of("sword"));
        when(chair.getItem()).thenReturn(chairComponent);
        when(chairComponent.getNameList()).thenReturn(Set.of("chair"));
        when(itemRepository.findByLocationRoom(eq(room))).thenReturn(List.of(sword, chair));

        AbstractCommand uut = new AbstractCommand(repositoryBundle, commService, applicationContext) {
            @Override
            public Question execute(Question question, WebSocketContext webSocketContext, List<Binding> bindings, Output output) {
                return question;
            }
        };

        assertTrue(uut.findRoomItem(ch, "sword").isPresent());
        assertTrue(uut.findRoomItem(ch, "sw").isPresent());
        assertTrue(uut.findRoomItem(ch, "chair").isPresent());
        assertTrue(uut.findRoomItem(ch, "ch").isPresent());
    }

    @Test
    void testFindRoomCharacter() {
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);
        when(targetCharacterComponent.getName()).thenReturn("Morgan");
        when(target.getCharacter()).thenReturn(targetCharacterComponent);
        when(characterRepository.findByLocationRoom(eq(room))).thenReturn(List.of(ch, target));

        AbstractCommand uut = new AbstractCommand(repositoryBundle, commService, applicationContext) {
            @Override
            public Question execute(Question question, WebSocketContext webSocketContext, List<Binding> bindings, Output output) {
                return question;
            }
        };

        assertTrue(uut.findRoomCharacter(ch, "Scion").isEmpty()); // can't find yourself
        assertTrue(uut.findRoomCharacter(ch, "Morgan").isPresent());
        assertTrue(uut.findRoomCharacter(ch, "morg").isPresent());
    }

    @Test
    void testFindWorldCharacter() {
        when(target.getCharacter()).thenReturn(targetCharacterComponent);
        when(target.getLocation()).thenReturn(targetLocationComponent);
        when(target.getCharacter().getName()).thenReturn("Morgan");
        when(characterRepository.findAll()).thenReturn(List.of(ch, target));

        AbstractCommand uut = new AbstractCommand(repositoryBundle, commService, applicationContext) {
            @Override
            public Question execute(Question question, WebSocketContext webSocketContext, List<Binding> bindings, Output output) {
                return question;
            }
        };

        assertTrue(uut.findWorldCharacter(ch, "Scion").isEmpty()); // can't find yourself
        assertTrue(uut.findWorldCharacter(ch, "Morgan").isPresent());
        assertTrue(uut.findWorldCharacter(ch, "morg").isPresent());
    }
}
