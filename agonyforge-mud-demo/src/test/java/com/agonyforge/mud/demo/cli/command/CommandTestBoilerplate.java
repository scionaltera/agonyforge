package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import com.agonyforge.mud.demo.model.repository.MudItemRepository;
import com.agonyforge.mud.demo.model.repository.MudRoomRepository;
import com.agonyforge.mud.demo.service.CommService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public abstract class CommandTestBoilerplate {
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

    @BeforeEach
    void baseSetUp() {
        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        lenient().when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
        lenient().when(repositoryBundle.getRoomRepository()).thenReturn(roomRepository);
    }
}
