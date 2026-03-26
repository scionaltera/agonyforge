package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.MudProperty;
import com.agonyforge.mud.demo.model.repository.MudPropertyRepository;
import com.agonyforge.mud.demo.service.CommService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.agonyforge.mud.demo.event.WeatherListener.PROPERTY_HOUR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TimeCommandTest {
    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private MudPropertyRepository propertyRepository;

    @Mock
    private CommService commService;

    @Mock
    private Question question;

    @Mock
    private WebSocketContext webSocketContext;

    @Mock
    private Binding commandBinding;

    @BeforeEach
    void setUp() {
        lenient().when(repositoryBundle.getPropertyRepository()).thenReturn(propertyRepository);
    }

    @Test
    void testGetTimeMidnight() {
        MudProperty mudHour = new MudProperty(PROPERTY_HOUR, "0");
        Output output = new Output();

        when(propertyRepository.findById(eq(PROPERTY_HOUR))).thenReturn(Optional.of(mudHour));

        TimeCommand uut = new TimeCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(question, webSocketContext, List.of(commandBinding), output);

        assertEquals(question, result);

        assertTrue(output.getOutput().get(0).contains("It is midnight"));
    }

    @Test
    void testGetTimeNoon() {
        MudProperty mudHour = new MudProperty(PROPERTY_HOUR, "12");
        Output output = new Output();

        when(propertyRepository.findById(eq(PROPERTY_HOUR))).thenReturn(Optional.of(mudHour));

        TimeCommand uut = new TimeCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(question, webSocketContext, List.of(commandBinding), output);

        assertEquals(question, result);

        assertTrue(output.getOutput().get(0).contains("It is noon"));
    }

    @ParameterizedTest
    @MethodSource
    void testGetTimeNight(String hour, String time) {
        MudProperty mudHour = new MudProperty(PROPERTY_HOUR, hour);
        Output output = new Output();

        when(propertyRepository.findById(eq(PROPERTY_HOUR))).thenReturn(Optional.of(mudHour));

        TimeCommand uut = new TimeCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(question, webSocketContext, List.of(commandBinding), output);

        assertEquals(question, result);

        assertTrue(output.getOutput().get(0).contains("The hour is " + time + " o'clock at night"));
    }

    private static Stream<Arguments> testGetTimeNight() {
        return Stream.of(
            Arguments.of("1", "1"),
            Arguments.of("2", "2"),
            Arguments.of("3", "3"),
            Arguments.of("4", "4"),
            Arguments.of("5", "5")
        );
    }

    @ParameterizedTest
    @MethodSource
    void testGetTimeMorning(String hour, String time) {
        MudProperty mudHour = new MudProperty(PROPERTY_HOUR, hour);
        Output output = new Output();

        when(propertyRepository.findById(eq(PROPERTY_HOUR))).thenReturn(Optional.of(mudHour));

        TimeCommand uut = new TimeCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(question, webSocketContext, List.of(commandBinding), output);

        assertEquals(question, result);

        assertTrue(output.getOutput().get(0).contains("The hour is " + time + " o'clock in the morning"));
    }

    private static Stream<Arguments> testGetTimeMorning() {
        return Stream.of(
            Arguments.of("6", "6"),
            Arguments.of("7", "7"),
            Arguments.of("8", "8"),
            Arguments.of("9", "9"),
            Arguments.of("10", "10")
        );
    }

    @ParameterizedTest
    @MethodSource
    void testGetTimeAfternoon(String hour, String time) {
        MudProperty mudHour = new MudProperty(PROPERTY_HOUR, hour);
        Output output = new Output();

        when(propertyRepository.findById(eq(PROPERTY_HOUR))).thenReturn(Optional.of(mudHour));

        TimeCommand uut = new TimeCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(question, webSocketContext, List.of(commandBinding), output);

        assertEquals(question, result);

        assertTrue(output.getOutput().get(0).contains("The hour is " + time + " o'clock in the afternoon"));
    }

    private static Stream<Arguments> testGetTimeAfternoon() {
        return Stream.of(
            Arguments.of("13", "1"),
            Arguments.of("14", "2"),
            Arguments.of("15", "3"),
            Arguments.of("16", "4"),
            Arguments.of("17", "5")
        );
    }

    @ParameterizedTest
    @MethodSource
    void testGetTimeEvening(String hour, String time) {
        MudProperty mudHour = new MudProperty(PROPERTY_HOUR, hour);
        Output output = new Output();

        when(propertyRepository.findById(eq(PROPERTY_HOUR))).thenReturn(Optional.of(mudHour));

        TimeCommand uut = new TimeCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(question, webSocketContext, List.of(commandBinding), output);

        assertEquals(question, result);

        assertTrue(output.getOutput().get(0).contains("The hour is " + time + " o'clock in the evening"));
    }

    private static Stream<Arguments> testGetTimeEvening() {
        return Stream.of(
            Arguments.of("18", "6"),
            Arguments.of("19", "7"),
            Arguments.of("20", "8"),
            Arguments.of("21", "9"),
            Arguments.of("22", "10"),
            Arguments.of("23", "11")
        );
    }
}
