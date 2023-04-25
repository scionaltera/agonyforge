package com.agonyforge.mud.demo.event;

import com.hazelcast.cluster.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.hazelcast.core.HazelcastInstance;

import java.util.Optional;
import java.util.UUID;

@Component
public class FightCoordinator {
    private static final Logger LOGGER = LoggerFactory.getLogger(FightCoordinator.class);

    private HazelcastInstance hazelcastInstance;

    @Autowired
    public FightCoordinator(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
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

        if (leaderOptional.isPresent() && leaderOptional.get().equals(me)) {
            LOGGER.info("leader={} me={}", leaderOptional.orElse(null), me);
        }
    }
}
