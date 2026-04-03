package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import com.agonyforge.mud.demo.model.repository.MudItemRepository;
import com.agonyforge.mud.demo.model.repository.MudRoomRepository;
import com.agonyforge.mud.demo.service.CommService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public abstract class CommandTestBoilerplate {
    private static final Random RANDOM = new Random();

    @Mock
    protected ApplicationContext applicationContext;

    @Mock
    protected WebSocketContext webSocketContext;

    @Mock
    protected CommService commService;

    @Mock
    protected RepositoryBundle repositoryBundle;

    @Mock
    protected MudCharacterRepository characterRepository;

    @Mock
    protected MudItemRepository itemRepository;

    @Mock
    protected MudRoomRepository roomRepository;

    @Mock
    protected Question question;

    @Mock
    protected Binding commandBinding;

    @Mock
    protected MudCharacter ch;

    protected final long CH_ID = RANDOM.nextLong();

    @BeforeEach
    void baseSetUp() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(MUD_CHARACTER, CH_ID);

        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        lenient().when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
        lenient().when(repositoryBundle.getRoomRepository()).thenReturn(roomRepository);

        lenient().when(characterRepository.findById(eq(CH_ID))).thenReturn(Optional.of(ch));

        lenient().when(webSocketContext.getAttributes()).thenReturn(attributes);
    }

    protected Random getRandom() {
        return RANDOM;
    }
}
