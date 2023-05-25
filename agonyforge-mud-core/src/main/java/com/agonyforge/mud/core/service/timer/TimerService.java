package com.agonyforge.mud.core.service.timer;

import com.agonyforge.mud.core.config.MqBrokerProperties;
import com.hazelcast.cluster.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.scheduling.annotation.Scheduled;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.stereotype.Controller;

import javax.annotation.PreDestroy;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * In a system with potentially multiple servers we need a way to coordinate things like fights across the whole
 * group so that they get resolved the same way no matter which server you happen to be connected to. This class
 * registers a STOMP client to several queues and listens for messages there. If this server is also the "leader"
 * (see comment in the code below because that probably doesn't mean what you think it means) it will generate
 * messages on the queue on fixed time intervals. One arbitrarily chosen server will get each message and publish
 * a corresponding application event.
 * <p>
 * The game code can register an event listener that would process one round of fights, without worrying about
 * the underlying complexity of server clustering. That spreads the load of processing across the whole cluster
 * but also avoids multiple servers processing the same fights at the same time. This pattern should be used for
 * anything that needs to be processed periodically in the game by only one server at a time.
 */
@Controller
public class TimerService implements StompSessionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimerService.class);
    private static final String DESTINATION_SECOND = "/queue/per_second";
    private static final String DESTINATION_MINUTE = "/queue/per_minute";
    private static final String DESTINATION_HOUR = "/queue/per_hour";
    private static final String DESTINATION_DAY = "/queue/per_day";


    private final ApplicationEventPublisher applicationEventPublisher;
    private final HazelcastInstance hazelcastInstance;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ReactorNettyTcpStompClient stompClient;
    private final MqBrokerProperties brokerProperties;

    private final Map<String, StompSession.Subscription> subscriptions = new HashMap<>();

    private boolean isBrokerAvailable = false;
    private StompSession stompSession = null;

    @Autowired
    public TimerService(ApplicationEventPublisher applicationEventPublisher,
                        HazelcastInstance hazelcastInstance,
                        SimpMessagingTemplate simpMessagingTemplate,
                        ReactorNettyTcpStompClient stompClient,
                        MqBrokerProperties brokerProperties) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.hazelcastInstance = hazelcastInstance;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.stompClient = stompClient;
        this.brokerProperties = brokerProperties;
    }

    @EventListener
    public void onApplicationEvent(BrokerAvailabilityEvent event) {
        isBrokerAvailable = event.isBrokerAvailable();

        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompHeaders stompHeaders = new StompHeaders();

        stompHeaders.setAcceptVersion("1.1", "1.2");
        stompHeaders.setLogin(brokerProperties.getClientUsername());
        stompHeaders.setPasscode(brokerProperties.getClientPassword());
        stompHeaders.setHeartbeat(new long[] {10000L, 10000L});
        stompHeaders.setSession("fight-coordinator");

        try {
            stompSession = stompClient.connectAsync(stompHeaders, this).get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Exception trying to connect: {}", e.getMessage(), e);
        }
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        LOGGER.info("Client connection to STOMP server established");

        subscriptions.put(DESTINATION_SECOND, stompSession.subscribe(DESTINATION_SECOND, this));
        subscriptions.put(DESTINATION_MINUTE, stompSession.subscribe(DESTINATION_MINUTE, this));
        subscriptions.put(DESTINATION_HOUR, stompSession.subscribe(DESTINATION_HOUR, this));
        subscriptions.put(DESTINATION_DAY, stompSession.subscribe(DESTINATION_DAY, this));
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        LOGGER.error("Exception while handling frame: session={} command={} headers={} payload={}",
            session.getSessionId(),
            command,
            headers,
            payload,
            exception);
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        LOGGER.error("Transport error: session={}", session.getSessionId(), exception);
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return TimerMessage.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        LOGGER.trace("Received timer message: headers={} payload={}", headers, payload);

        if (null == headers.getDestination()) {
            LOGGER.error("Message destination is null!");
            return;
        }

        TimerEvent event = null;

        switch(headers.getDestination()) {
            case DESTINATION_SECOND -> event = new TimerEvent(this, TimeUnit.SECONDS);
            case DESTINATION_MINUTE -> event = new TimerEvent(this, TimeUnit.MINUTES);
            case DESTINATION_HOUR -> event = new TimerEvent(this, TimeUnit.HOURS);
            case DESTINATION_DAY -> event = new TimerEvent(this, TimeUnit.DAYS);
            default -> LOGGER.error("Unknown message destination!");
        }

        if (event != null) {
            applicationEventPublisher.publishEvent(event);
        }
    }

    @PreDestroy
    void onShutdown() {
        for (String key : subscriptions.keySet()) {
            subscriptions.get(key).unsubscribe();
        }

        if (stompSession != null) {
            stompSession.disconnect();
        }
    }

    @Scheduled(fixedRate = 1L, timeUnit = TimeUnit.SECONDS)
    public void doPerSecond() {
        doTimer(DESTINATION_SECOND);
    }

    @Scheduled(fixedRate = 1L, timeUnit = TimeUnit.MINUTES)
    public void doPerMinute() {
        doTimer(DESTINATION_MINUTE);
    }

    @Scheduled(fixedRate = 1L, timeUnit = TimeUnit.HOURS)
    public void doPerHour() {
        doTimer(DESTINATION_HOUR);
    }

    @Scheduled(fixedRate = 1L, timeUnit = TimeUnit.DAYS)
    public void doPerDay() {
        doTimer(DESTINATION_DAY);
    }

    private void doTimer(final String destination) {
        // "leader" is a little bit of a misnomer here
        // it's not actually the elected leader of the Hazelcast cluster
        // apparently you can't even get that info via public API?!
        //
        // it's just the UUID that is alphabetically first in the list
        // who cares? it doesn't matter!
        // all we actually need is for everybody to be able to independently pick the same one

        UUID me = hazelcastInstance.getCluster().getLocalMember().getUuid();
        Optional<UUID> leaderOptional = hazelcastInstance.getCluster().getMembers()
            .stream()
            .map(Member::getUuid)
            .sorted()
            .findFirst();

        if (isBrokerAvailable && leaderOptional.isPresent() && leaderOptional.get().equals(me)) {
            LOGGER.debug("leader={} me={}", leaderOptional.orElse(null), me);

            MessageHeaders messageHeaders = SimpMessageHeaderAccessor.create().getMessageHeaders();
            TimerMessage timerMessage = new TimerMessage(System.currentTimeMillis());

            simpMessagingTemplate.convertAndSend(destination, timerMessage, messageHeaders);
        }
    }
}
