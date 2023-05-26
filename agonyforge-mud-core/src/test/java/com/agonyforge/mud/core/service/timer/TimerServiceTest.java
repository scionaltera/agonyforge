package com.agonyforge.mud.core.service.timer;

import com.agonyforge.mud.core.config.MqBrokerProperties;
import com.hazelcast.cluster.Cluster;
import com.hazelcast.cluster.Member;
import com.hazelcast.core.HazelcastInstance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.messaging.simp.stomp.ReactorNettyTcpStompClient;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static com.agonyforge.mud.core.service.timer.TimerService.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TimerServiceTest {
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private HazelcastInstance hazelcastInstance;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private ReactorNettyTcpStompClient stompClient;

    @Mock
    private MqBrokerProperties brokerProperties;

    @Mock
    private BrokerAvailabilityEvent brokerAvailabilityEvent;

    @Mock
    private StompSession stompSession;

    @Mock
    private StompHeaders stompHeaders;

    @Captor
    private ArgumentCaptor<StompHeaders> headersCaptor;

    @Captor
    private ArgumentCaptor<TimerEvent> timerEventCaptor;

    @Test
    void testOnApplicationEvent() {
        String username = "user";
        String password = "pass";

        when(brokerProperties.getClientUsername()).thenReturn(username);
        when(brokerProperties.getClientPassword()).thenReturn(password);
        when(brokerAvailabilityEvent.isBrokerAvailable()).thenReturn(true);
        when(stompClient.connectAsync(any(StompHeaders.class), any(TimerService.class)))
            .thenReturn(CompletableFuture.completedFuture(stompSession));

        TimerService uut = new TimerService(
            applicationEventPublisher,
            hazelcastInstance,
            simpMessagingTemplate,
            stompClient,
            brokerProperties
        );

        uut.onApplicationEvent(brokerAvailabilityEvent);

        verify(stompClient).setMessageConverter(any(MappingJackson2MessageConverter.class));
        verify(stompClient).connectAsync(headersCaptor.capture(), eq(uut));

        StompHeaders headers = headersCaptor.getValue();

        assertEquals("1.1", Objects.requireNonNull(headers.getAcceptVersion())[0]);
        assertEquals("1.2", Objects.requireNonNull(headers.getAcceptVersion())[1]);
        assertEquals(username, headers.getLogin());
        assertEquals(password, headers.getPasscode());
        assertEquals(10000L, Objects.requireNonNull(headers.getHeartbeat())[0]);
        assertEquals(10000L, Objects.requireNonNull(headers.getHeartbeat())[1]);
        assertEquals(STOMP_SESSION_NAME, headers.getSession());
    }

    @Test
    void testAfterConnected() {
        StompSession.Subscription subscription = mock(StompSession.Subscription.class);

        when(stompSession.subscribe(anyString(), any(TimerService.class))).thenReturn(subscription);

        TimerService uut = new TimerService(
            applicationEventPublisher,
            hazelcastInstance,
            simpMessagingTemplate,
            stompClient,
            brokerProperties
        );

        uut.afterConnected(stompSession, stompHeaders);

        verify(stompSession, times(4)).subscribe(anyString(), eq(uut));

        assertNotNull(uut.getSubscription(DESTINATION_SECOND));
        assertNotNull(uut.getSubscription(DESTINATION_MINUTE));
        assertNotNull(uut.getSubscription(DESTINATION_HOUR));
        assertNotNull(uut.getSubscription(DESTINATION_DAY));
    }

    @Test
    void testGetPayloadType() {
        TimerService uut = new TimerService(
            applicationEventPublisher,
            hazelcastInstance,
            simpMessagingTemplate,
            stompClient,
            brokerProperties
        );

        assertEquals(TimerMessage.class, uut.getPayloadType(stompHeaders));
    }

    @ParameterizedTest
    @MethodSource
    void testHandleFrame(String destination, TimeUnit timeUnit) {
        when(stompHeaders.getDestination()).thenReturn(destination);

        TimerMessage message = new TimerMessage(System.currentTimeMillis());

        TimerService uut = new TimerService(
            applicationEventPublisher,
            hazelcastInstance,
            simpMessagingTemplate,
            stompClient,
            brokerProperties
        );

        uut.handleFrame(stompHeaders, message);

        verify(applicationEventPublisher).publishEvent(timerEventCaptor.capture());

        TimerEvent event = timerEventCaptor.getValue();

        assertEquals(uut, event.getSource());
        assertEquals(timeUnit, event.getFrequency());
    }

    private static Stream<Arguments> testHandleFrame() {
        return Stream.of(
            Arguments.of(DESTINATION_SECOND, TimeUnit.SECONDS),
            Arguments.of(DESTINATION_MINUTE, TimeUnit.MINUTES),
            Arguments.of(DESTINATION_HOUR, TimeUnit.HOURS),
            Arguments.of(DESTINATION_DAY, TimeUnit.DAYS)
        );
    }

    private void setupHazelcastInstance(int clusterSize) {
        Cluster cluster = mock(Cluster.class);
        Set<Member> members = new HashSet<>();

        for (int i = 0; i < clusterSize; i++) {
            Member member = mock(Member.class);

            when(member.getUuid()).thenReturn(UUID.randomUUID());
            members.add(member);
        }

        // opposite of what doTimer() does, picks the last one instead of first
        // so if clusterSize is 1 we're the leader
        // and if it's > 1 we know we aren't the leader
        Member me = members
            .stream()
            .max(Comparator.comparing(Member::getUuid))
            .orElseThrow();

        when(hazelcastInstance.getCluster()).thenReturn(cluster);
        when(cluster.getMembers()).thenReturn(members);
        when(cluster.getLocalMember()).thenReturn(me);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "SECONDS",
        "MINUTES",
        "HOURS",
        "DAYS"
    })
    void testLeaderNoBroker(String timeUnit) {
        setupHazelcastInstance(1);

        TimerService uut = new TimerService(
            applicationEventPublisher,
            hazelcastInstance,
            simpMessagingTemplate,
            stompClient,
            brokerProperties
        );

        uut.setBrokerAvailability(false);

        switch(timeUnit) {
            case "SECONDS" -> uut.doPerSecond();
            case "MINUTES" -> uut.doPerMinute();
            case "HOURS" -> uut.doPerHour();
            case "DAYS" -> uut.doPerDay();
        }

        verify(hazelcastInstance, times(2)).getCluster();
        verify(simpMessagingTemplate, never()).convertAndSend(anyString(), any(TimerMessage.class), any(MessageHeaders.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "SECONDS",
        "MINUTES",
        "HOURS",
        "DAYS"
    })
    void testMemberNoBroker(String timeUnit) {
        setupHazelcastInstance(2);

        TimerService uut = new TimerService(
            applicationEventPublisher,
            hazelcastInstance,
            simpMessagingTemplate,
            stompClient,
            brokerProperties
        );

        uut.setBrokerAvailability(false);

        switch(timeUnit) {
            case "SECONDS" -> uut.doPerSecond();
            case "MINUTES" -> uut.doPerMinute();
            case "HOURS" -> uut.doPerHour();
            case "DAYS" -> uut.doPerDay();
        }

        verify(hazelcastInstance, times(2)).getCluster();
        verify(simpMessagingTemplate, never()).convertAndSend(anyString(), any(TimerMessage.class), any(MessageHeaders.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "SECONDS",
        "MINUTES",
        "HOURS",
        "DAYS"
    })
    void testLeaderYesBroker(String timeUnit) {
        setupHazelcastInstance(1);

        TimerService uut = new TimerService(
            applicationEventPublisher,
            hazelcastInstance,
            simpMessagingTemplate,
            stompClient,
            brokerProperties
        );

        uut.setBrokerAvailability(true);

        switch(timeUnit) {
            case "SECONDS" -> uut.doPerSecond();
            case "MINUTES" -> uut.doPerMinute();
            case "HOURS" -> uut.doPerHour();
            case "DAYS" -> uut.doPerDay();
        }

        verify(hazelcastInstance, times(2)).getCluster();
        verify(simpMessagingTemplate).convertAndSend(anyString(), any(TimerMessage.class), any(MessageHeaders.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "SECONDS",
        "MINUTES",
        "HOURS",
        "DAYS"
    })
    void testMemberYesBroker(String timeUnit) {
        setupHazelcastInstance(2);

        TimerService uut = new TimerService(
            applicationEventPublisher,
            hazelcastInstance,
            simpMessagingTemplate,
            stompClient,
            brokerProperties
        );

        uut.setBrokerAvailability(true);

        switch(timeUnit) {
            case "SECONDS" -> uut.doPerSecond();
            case "MINUTES" -> uut.doPerMinute();
            case "HOURS" -> uut.doPerHour();
            case "DAYS" -> uut.doPerDay();
        }

        verify(hazelcastInstance, times(2)).getCluster();
        verify(simpMessagingTemplate, never()).convertAndSend(anyString(), any(TimerMessage.class), any(MessageHeaders.class));
    }
}
