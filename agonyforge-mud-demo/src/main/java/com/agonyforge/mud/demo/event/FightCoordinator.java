package com.agonyforge.mud.demo.event;

import com.hazelcast.cluster.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.scheduling.annotation.Scheduled;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.stereotype.Controller;

import java.util.Optional;
import java.util.UUID;

@Controller
public class FightCoordinator implements ApplicationListener<BrokerAvailabilityEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FightCoordinator.class);

    private HazelcastInstance hazelcastInstance;
    private SimpMessagingTemplate simpMessagingTemplate;
    private boolean isBrokerAvailable = false;

    @Autowired
    public FightCoordinator(HazelcastInstance hazelcastInstance, SimpMessagingTemplate simpMessagingTemplate) {
        this.hazelcastInstance = hazelcastInstance;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void onApplicationEvent(BrokerAvailabilityEvent event) {
        isBrokerAvailable = event.isBrokerAvailable();
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
            LOGGER.info("leader={} me={}", leaderOptional.orElse(null), me);

            MessageHeaders messageHeaders = SimpMessageHeaderAccessor.create().getMessageHeaders();

            simpMessagingTemplate.convertAndSend("/fight", "fight1", messageHeaders);
            simpMessagingTemplate.convertAndSend("/app/fight", "fight2", messageHeaders);
            simpMessagingTemplate.convertAndSend("/topic/fight", "fight3", messageHeaders);
            simpMessagingTemplate.convertAndSend("/queue/fight", "fight4", messageHeaders);
        }
    }
}
