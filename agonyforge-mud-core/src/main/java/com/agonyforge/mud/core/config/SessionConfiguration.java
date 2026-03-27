package com.agonyforge.mud.core.config;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.session.HazelcastIndexedSessionRepository;
import com.hazelcast.spring.session.HazelcastSessionConfiguration;
import com.hazelcast.spring.session.config.annotation.SpringSessionHazelcastInstance;
import com.hazelcast.spring.session.config.annotation.web.http.EnableHazelcastHttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.FlushMode;
import org.springframework.session.SaveMode;
import org.springframework.session.config.SessionRepositoryCustomizer;


import java.time.Duration;

import static com.hazelcast.spring.session.HazelcastIndexedSessionRepository.DEFAULT_SESSION_MAP_NAME;

/*
 * Based on https://docs.hazelcast.com/tutorials/spring-session-hazelcast
 */
@Configuration
@EnableHazelcastHttpSession
public class SessionConfiguration {
    public static final String MUD_QUESTION = "MUD.QUESTION";
    public static final String MUD_CHARACTER = "MUD.CHARACTER";

    @Bean
    @SpringSessionHazelcastInstance
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();

        config.setClusterName("mud-sessions");
        HazelcastSessionConfiguration.applySerializationConfig(config);

        return Hazelcast.newHazelcastInstance(config);
    }

    @Bean
    public SessionRepositoryCustomizer<HazelcastIndexedSessionRepository> customize() {
        return (sessionRepository) -> {
            sessionRepository.setFlushMode(FlushMode.IMMEDIATE);
            sessionRepository.setSaveMode(SaveMode.ON_SET_ATTRIBUTE);
            sessionRepository.setSessionMapName(DEFAULT_SESSION_MAP_NAME);
            sessionRepository.setDefaultMaxInactiveInterval(Duration.ofDays(1));
        };
    }
}
