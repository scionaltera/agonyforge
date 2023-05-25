package com.agonyforge.mud.demo.event.fight;

import com.agonyforge.mud.core.config.MqBrokerProperties;
import com.hazelcast.cluster.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * In a system with potentially multiple servers we need a way to coordinate things like fights across the whole
 * group so that they get resolved the same way no matter which server you happen to be connected to. This class
 * registers a STOMP client to /queue/fight and listens for messages there. If this server is also the "leader"
 * (see comment in the code below because that probably doesn't mean what you think it means) it will generate
 * messages on the queue periodically. One arbitrarily chosen server will get each message and process the fights
 * for that round. That spreads the load of processing across the whole cluster but also avoids multiple servers
 * processing the same fights at the same time. This pattern should be used for anything that needs to be processed
 * periodically in the game but shouldn't be duplicated.
 * <p>
 * A more scalable approach could be to split the list of fights equally across all the servers and have them each
 * do their own batch every tick.
 */
@Controller
public class FightCoordinator implements StompSessionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(FightCoordinator.class);
    private static final String DESTINATION_FIGHT = "/queue/fight";

    private final HazelcastInstance hazelcastInstance;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ReactorNettyTcpStompClient stompClient;
    private final MqBrokerProperties brokerProperties;

    private final Map<String, StompSession.Subscription> subscriptions = new HashMap<>();

    private boolean isBrokerAvailable = false;
    private StompSession stompSession = null;

    @Autowired
    public FightCoordinator(HazelcastInstance hazelcastInstance,
                            SimpMessagingTemplate simpMessagingTemplate,
                            ReactorNettyTcpStompClient stompClient,
                            MqBrokerProperties brokerProperties) {
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

        subscriptions.put(DESTINATION_FIGHT, stompSession.subscribe(DESTINATION_FIGHT, this));
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
        LOGGER.error("Got a transport error: session={}", session.getSessionId(), exception);
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return FightMessage.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        LOGGER.debug("Got a new message: {}", payload);

        // TODO process the ongoing fights
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

    @Scheduled(cron = "0/1 * * * * ?")
    public void doFights() {
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
            FightMessage fightMessage = new FightMessage();

            // TODO put something useful in here
            fightMessage.setMessage("foightin round the world");

            simpMessagingTemplate.convertAndSend(DESTINATION_FIGHT, fightMessage, messageHeaders);
        }
    }
}
