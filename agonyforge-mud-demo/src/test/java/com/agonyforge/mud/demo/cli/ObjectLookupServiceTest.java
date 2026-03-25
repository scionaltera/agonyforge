package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.demo.model.constant.AdminFlag;
import com.agonyforge.mud.demo.model.constant.Effort;
import com.agonyforge.mud.demo.model.constant.Stat;
import com.agonyforge.mud.demo.model.constant.WearSlot;
import com.agonyforge.mud.demo.model.impl.*;
import com.agonyforge.mud.demo.model.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ObjectLookupServiceTest {
    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private CommandRepository commandRepository;

    @Mock
    private MudCharacter ch, other, goblin;

    @Mock
    private MudItem sword;

    @Mock
    private MudRoom chRoom;

    @Mock
    private LocationComponent chLocation, otherLocation, goblinLocation, swordLocation;

    @Mock
    private CharacterComponent chComponent, otherComponent, goblinComponent;

    @Mock
    private PlayerComponent chPlayer, otherPlayer;

    @Mock
    private ItemComponent swordItem;

    @BeforeEach
    public void setup() {
        lenient().when(ch.getCharacter()).thenReturn(chComponent);
        lenient().when(chComponent.getName()).thenReturn("Scion");
        lenient().when(ch.getPlayer()).thenReturn(chPlayer);
        lenient().when(ch.getLocation()).thenReturn(chLocation);
        lenient().when(chLocation.getRoom()).thenReturn(chRoom);

        lenient().when(other.getCharacter()).thenReturn(otherComponent);
        lenient().when(otherComponent.getName()).thenReturn("Other");
        lenient().when(other.getPlayer()).thenReturn(otherPlayer);
        lenient().when(other.getLocation()).thenReturn(otherLocation);
        lenient().when(otherLocation.getRoom()).thenReturn(chRoom);

        lenient().when(goblin.getCharacter()).thenReturn(goblinComponent);
        lenient().when(goblinComponent.getName()).thenReturn("a goblin");
        lenient().when(goblin.getLocation()).thenReturn(goblinLocation);
        lenient().when(goblinLocation.getRoom()).thenReturn(chRoom);

        lenient().when(sword.getItem()).thenReturn(swordItem);
        lenient().when(swordItem.getNameList()).thenReturn(Set.of("sword"));
        lenient().when(sword.getLocation()).thenReturn(swordLocation);
    }

    @Test
    public void testSizeMismatch() {
        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);

        List<Binding> result = uut.bind(
            ch,
            null,
            List.of("token"),
            List.of(TokenType.WORD, TokenType.WORD));

        assertTrue(result.isEmpty());
        verifyNoInteractions(repositoryBundle, commandRepository);
    }

    @Test
    public void testUnknownBinding() {
        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);
        List<Binding> result = uut.bind(
            ch,
            null,
            List.of("token"),
            List.of(TokenType.INVALID));

        assertTrue(result.isEmpty());
        verifyNoInteractions(repositoryBundle, commandRepository);
    }

    @Test
    public void testBindCommand() {
        CommandReference mockRef = mock(CommandReference.class);

        when(commandRepository.findFirstByNameStartingWith(eq("SAY"), any(Sort.class))).thenReturn(Optional.of(mockRef));

        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);
        List<Binding> result = uut.bind(
            ch,
            null,
            List.of("say"),
            List.of(TokenType.COMMAND));

        CommandReference ref = result.get(0).asCommandReference();

        assertEquals(1, result.size());
        assertNotNull(ref);
    }

    @Test
    public void testBindCommandNotFound() {
        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);

        try {
            uut.bind(
                ch,
                null,
                List.of("say"),
                List.of(TokenType.COMMAND));
        } catch (IllegalArgumentException e) {
            return;
        }

        fail();
    }

    @Test
    public void testBindWord() {
        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);
        List<Binding> result = uut.bind(
            ch,
            null,
            List.of("test"),
            List.of(TokenType.WORD));

        String word = result.get(0).asString();

        assertEquals(1, result.size());
        assertEquals("test", word);
    }

    @Test
    public void testBindQuotedWords() {
        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);
        List<Binding> result = uut.bind(
            ch,
            null,
            List.of("I am the very model of a modern major general."),
            List.of(TokenType.QUOTED_WORDS));

        String word = result.get(0).asString();

        assertEquals(1, result.size());
        assertEquals("I am the very model of a modern major general.", word);
    }

    @Test
    public void testBindNumber() {
        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);
        List<Binding> result = uut.bind(
            ch,
            null,
            List.of("22000"),
            List.of(TokenType.NUMBER));

        Long number = result.get(0).asNumber();

        assertEquals(1, result.size());
        assertEquals(22000, number);
    }

    @Test
    public void testBindNumberInvalid() {
        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);

        try {
            List<Binding> result = uut.bind(
                ch,
                null,
                List.of("word"),
                List.of(TokenType.NUMBER));

            result.get(0).asNumber();

        } catch (IllegalArgumentException e) {
            return;
        }

        fail();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "STR", "INT", "WIS", "DEX", "CON", "CHA"
    })
    public void testBindStat(String name) {
        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);
        List<Binding> result = uut.bind(
            ch,
            null,
            List.of(name),
            List.of(TokenType.STAT));

        Stat stat = result.get(0).asStat();

        assertEquals(1, result.size());
        assertTrue(Arrays.asList(Stat.values()).contains(stat));
    }

    @Test
    public void testBindStatInvalid() {
        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);

        try {
            List<Binding> result = uut.bind(
                ch,
                null,
                List.of("TST"),
                List.of(TokenType.STAT));

            result.get(0).asStat();

        } catch (IllegalArgumentException e) {
            return;
        }

        fail();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Basic",
        "Weapons & Tools",
        "Guns",
        "Energy & Magic",
        "Ultimate"
    })
    public void testBindEffort(String name) {
        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);
        List<Binding> result = uut.bind(
            ch,
            null,
            List.of(name),
            List.of(TokenType.EFFORT));

        Effort effort = result.get(0).asEffort();

        assertEquals(1, result.size());
        assertTrue(Arrays.asList(Effort.values()).contains(effort));
    }

    @Test
    public void testBindEffortInvalid() {
        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);

        try {
            List<Binding> result = uut.bind(
                ch,
                null,
                List.of("Test"),
                List.of(TokenType.EFFORT));

            result.get(0).asEffort();

        } catch (IllegalArgumentException e) {
            return;
        }

        fail();
    }

    @Test
    public void testBindCharacterInRoom() {
        MudCharacterRepository characterRepository = mock(MudCharacterRepository.class);

        when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        when(characterRepository.findByLocationRoom(eq(chRoom))).thenReturn(List.of(ch, other, goblin));

        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);
        List<Binding> result = uut.bind(
            ch,
            null,
            List.of("goblin"),
            List.of(TokenType.CHARACTER_IN_ROOM));

        MudCharacter target = result.get(0).asCharacter();

        assertEquals(1, result.size());
        assertEquals(goblin, target);
    }

    @Test
    public void testBindCharacterInRoomNotFound() {
        MudCharacterRepository characterRepository = mock(MudCharacterRepository.class);

        when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        when(characterRepository.findByLocationRoom(eq(chRoom))).thenReturn(List.of(ch, other));

        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);

        try {
            uut.bind(
                ch,
                null,
                List.of("goblin"),
                List.of(TokenType.CHARACTER_IN_ROOM));
        } catch (IllegalArgumentException e) {
            return;
        }

        fail();
    }

    @Test
    public void testBindPlayerInRoom() {
        MudCharacterRepository characterRepository = mock(MudCharacterRepository.class);

        when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        when(characterRepository.findByLocationRoom(eq(chRoom))).thenReturn(List.of(ch, other, goblin));

        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);
        List<Binding> result = uut.bind(
            ch,
            null,
            List.of("other"),
            List.of(TokenType.PLAYER_IN_ROOM));

        MudCharacter target = result.get(0).asCharacter();

        assertEquals(1, result.size());
        assertEquals(other, target);
    }

    @Test
    public void testBindPlayerInRoomNotFound() {
        MudCharacterRepository characterRepository = mock(MudCharacterRepository.class);

        when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        when(characterRepository.findByLocationRoom(eq(chRoom))).thenReturn(List.of(ch, goblin));

        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);

        try {
            uut.bind(
                ch,
                null,
                List.of("other"),
                List.of(TokenType.PLAYER_IN_ROOM));
        } catch (IllegalArgumentException e) {
            return;
        }

        fail();
    }

    @Test
    public void testBindNpcInRoom() {
        MudCharacterRepository characterRepository = mock(MudCharacterRepository.class);

        when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        when(characterRepository.findByLocationRoom(eq(chRoom))).thenReturn(List.of(ch, other, goblin));

        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);
        List<Binding> result = uut.bind(
            ch,
            null,
            List.of("goblin"),
            List.of(TokenType.NPC_IN_ROOM));

        MudCharacter target = result.get(0).asCharacter();

        assertEquals(1, result.size());
        assertEquals(goblin, target);
    }

    @Test
    public void testBindNpcInRoomNotFound() {
        MudCharacterRepository characterRepository = mock(MudCharacterRepository.class);

        when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        when(characterRepository.findByLocationRoom(eq(chRoom))).thenReturn(List.of(ch, other));

        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);

        try {
            uut.bind(
                ch,
                null,
                List.of("goblin"),
                List.of(TokenType.NPC_IN_ROOM));
        } catch (IllegalArgumentException e) {
            return;
        }

        fail();
    }

    @Test
    public void testBindCharacterInWorld() {
        MudCharacterRepository characterRepository = mock(MudCharacterRepository.class);

        when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        when(characterRepository.findAll()).thenReturn(List.of(ch, other, goblin));

        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);
        List<Binding> result = uut.bind(
            ch,
            null,
            List.of("goblin"),
            List.of(TokenType.CHARACTER_IN_WORLD));

        MudCharacter target = result.get(0).asCharacter();

        assertEquals(1, result.size());
        assertEquals(goblin, target);
    }

    @Test
    public void testBindCharacterInWorldNotFound() {
        MudCharacterRepository characterRepository = mock(MudCharacterRepository.class);

        when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        when(characterRepository.findAll()).thenReturn(List.of(ch, other, goblin));

        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);

        try {
            uut.bind(
                ch,
                null,
                List.of("harpy"),
                List.of(TokenType.CHARACTER_IN_WORLD));
        } catch (IllegalArgumentException e) {
            return;
        }

        fail();
    }

    @Test
    public void testBindPlayerInWorld() {
        MudCharacterRepository characterRepository = mock(MudCharacterRepository.class);

        when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        when(characterRepository.findAll()).thenReturn(List.of(ch, other, goblin));

        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);
        List<Binding> result = uut.bind(
            ch,
            null,
            List.of("other"),
            List.of(TokenType.PLAYER_IN_WORLD));

        MudCharacter target = result.get(0).asCharacter();

        assertEquals(1, result.size());
        assertEquals(other, target);
    }

    @Test
    public void testBindPlayerInWorldNotFound() {
        MudCharacterRepository characterRepository = mock(MudCharacterRepository.class);

        when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        when(characterRepository.findAll()).thenReturn(List.of(ch, goblin));

        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);

        try {
            uut.bind(
                ch,
                null,
                List.of("other"),
                List.of(TokenType.PLAYER_IN_WORLD));
        } catch (IllegalArgumentException e) {
            return;
        }

        fail();
    }

    @Test
    public void testBindNpcInWorld() {
        MudCharacterRepository characterRepository = mock(MudCharacterRepository.class);

        when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        when(characterRepository.findAll()).thenReturn(List.of(ch, other, goblin));

        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);
        List<Binding> result = uut.bind(
            ch,
            null,
            List.of("goblin"),
            List.of(TokenType.NPC_IN_WORLD));

        MudCharacter target = result.get(0).asCharacter();

        assertEquals(1, result.size());
        assertEquals(goblin, target);
    }

    @Test
    public void testBindNpcInWorldNotFound() {
        MudCharacterRepository characterRepository = mock(MudCharacterRepository.class);

        when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        when(characterRepository.findAll()).thenReturn(List.of(ch, other));

        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);

        try {
            uut.bind(
                ch,
                null,
                List.of("goblin"),
                List.of(TokenType.NPC_IN_WORLD));
        } catch (IllegalArgumentException e) {
            return;
        }

        fail();
    }

    @Test
    public void testBindItemHeld() {
        MudItemRepository itemRepository = mock(MudItemRepository.class);

        when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
        when(itemRepository.findByLocationHeld(eq(ch))).thenReturn(List.of(sword));

        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);
        List<Binding> result = uut.bind(
            ch,
            null,
            List.of("sword"),
            List.of(TokenType.ITEM_HELD));

        MudItem item = result.get(0).asItem();

        assertEquals(1, result.size());
        assertEquals(item, sword);
    }

    @Test
    public void testBindItemHeldNotFound() {
        MudItemRepository itemRepository = mock(MudItemRepository.class);

        when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
        when(itemRepository.findByLocationHeld(eq(ch))).thenReturn(List.of());

        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);

        try {
            uut.bind(
                ch,
                null,
                List.of("sword"),
                List.of(TokenType.ITEM_HELD));
        } catch (IllegalArgumentException e) {
            return;
        }

        fail();
    }

    @Test
    public void testBindItemWorn() {
        MudItemRepository itemRepository = mock(MudItemRepository.class);

        when(swordLocation.getWorn()).thenReturn(EnumSet.of(WearSlot.HELD_MAIN));
        when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
        when(itemRepository.findByLocationHeld(eq(ch))).thenReturn(List.of(sword));

        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);
        List<Binding> result = uut.bind(
            ch,
            null,
            List.of("sword"),
            List.of(TokenType.ITEM_WORN));

        MudItem item = result.get(0).asItem();

        assertEquals(1, result.size());
        assertEquals(item, sword);
    }

    @Test
    public void testBindItemWornNotFound() {
        MudItemRepository itemRepository = mock(MudItemRepository.class);

        when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
        when(itemRepository.findByLocationHeld(eq(ch))).thenReturn(List.of());

        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);

        try {
            uut.bind(
                ch,
                null,
                List.of("sword"),
                List.of(TokenType.ITEM_WORN));
        } catch (IllegalArgumentException e) {
            return;
        }

        fail();
    }

    @Test
    public void testBindItemGround() {
        MudItemRepository itemRepository = mock(MudItemRepository.class);

        when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
        when(itemRepository.findByLocationRoom(eq(chRoom))).thenReturn(List.of(sword));

        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);
        List<Binding> result = uut.bind(
            ch,
            null,
            List.of("sword"),
            List.of(TokenType.ITEM_GROUND));

        MudItem item = result.get(0).asItem();

        assertEquals(1, result.size());
        assertEquals(item, sword);
    }

    @Test
    public void testBindItemGroundNotFound() {
        MudItemRepository itemRepository = mock(MudItemRepository.class);

        when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
        when(itemRepository.findByLocationRoom(eq(chRoom))).thenReturn(List.of());

        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);

        try {
            uut.bind(
                ch,
                null,
                List.of("sword"),
                List.of(TokenType.ITEM_GROUND));
        } catch (IllegalArgumentException e) {
            return;
        }

        fail();
    }

    @Test
    public void testBindNpc() {
        MudCharacterPrototypeRepository templateRepository = mock(MudCharacterPrototypeRepository.class);
        MudCharacterTemplate mockTemplate = mock(MudCharacterTemplate.class);

        when(repositoryBundle.getCharacterPrototypeRepository()).thenReturn(templateRepository);
        when(templateRepository.findById(eq(42L))).thenReturn(Optional.of(mockTemplate));

        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);
        List<Binding> result = uut.bind(
            ch,
            null,
            List.of("42"),
            List.of(TokenType.NPC_ID));

        MudCharacterTemplate template = result.get(0).asCharacterTemplate();

        assertEquals(template, mockTemplate);
    }

    @Test
    public void testBindNpcNotFound() {
        MudCharacterPrototypeRepository templateRepository = mock(MudCharacterPrototypeRepository.class);

        when(repositoryBundle.getCharacterPrototypeRepository()).thenReturn(templateRepository);

        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);

        try {
            uut.bind(
                ch,
                null,
                List.of("42"),
                List.of(TokenType.NPC_ID));
        } catch (IllegalArgumentException e) {
            return;
        }

        fail();
    }

    @Test
    public void testBindRoom() {
        MudRoomRepository roomRepository = mock(MudRoomRepository.class);
        MudRoom mockRoom = mock(MudRoom.class);

        when(repositoryBundle.getRoomRepository()).thenReturn(roomRepository);
        when(roomRepository.findById(eq(42L))).thenReturn(Optional.of(mockRoom));

        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);
        List<Binding> result = uut.bind(
            ch,
            null,
            List.of("42"),
            List.of(TokenType.ROOM_ID)
        );

        MudRoom room = result.get(0).asRoom();

        assertEquals(room, mockRoom);
    }

    @Test
    public void testBindRoomNotFound() {
        MudRoomRepository roomRepository = mock(MudRoomRepository.class);

        when(repositoryBundle.getRoomRepository()).thenReturn(roomRepository);

        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);

        try {
            uut.bind(
                ch,
                null,
                List.of("42"),
                List.of(TokenType.ROOM_ID)
            );
        } catch (IllegalArgumentException e) {
            return;
        }

        fail();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "HOLYLIGHT",
        "PEACEFUL"
    })
    public void testBindAdminFlag(String name) {
        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);
        List<Binding> result = uut.bind(
            ch,
            null,
            List.of(name),
            List.of(TokenType.ADMIN_FLAG)
        );

        AdminFlag flag = result.get(0).asAdminFlag();

        assertEquals(1, result.size());
        assertTrue(Arrays.asList(AdminFlag.values()).contains(flag));
    }

    @Test
    public void testBindAdminFlagNotFound() {
        ObjectLookupService uut = new ObjectLookupService(repositoryBundle, commandRepository);

        try {
            uut.bind(
                ch,
                null,
                List.of("INVALID"),
                List.of(TokenType.ADMIN_FLAG)
            );
        } catch (IllegalArgumentException e) {
            return;
        }

        fail();
    }
}
