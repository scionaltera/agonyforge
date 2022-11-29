package com.agonyforge.mud.core.config;

import com.hazelcast.config.AttributeConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.IndexConfig;
import com.hazelcast.config.IndexType;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.FlushMode;
import org.springframework.session.MapSession;
import org.springframework.session.SaveMode;
import org.springframework.session.config.SessionRepositoryCustomizer;
import org.springframework.session.hazelcast.HazelcastIndexedSessionRepository;
import org.springframework.session.hazelcast.PrincipalNameExtractor;
import org.springframework.session.hazelcast.HazelcastSessionSerializer;
import org.springframework.session.hazelcast.config.annotation.SpringSessionHazelcastInstance;
import org.springframework.session.hazelcast.config.annotation.web.http.EnableHazelcastHttpSession;

import java.time.Duration;

import static org.springframework.session.hazelcast.HazelcastIndexedSessionRepository.DEFAULT_SESSION_MAP_NAME;

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
        AttributeConfig attributeConfig = new AttributeConfig()
            .setName(HazelcastIndexedSessionRepository.PRINCIPAL_NAME_ATTRIBUTE)
            .setExtractorClassName(PrincipalNameExtractor.class.getName());

        config.setClusterName("mud-sessions");
        config.getMapConfig(DEFAULT_SESSION_MAP_NAME)
            .addAttributeConfig(attributeConfig)
            .addIndexConfig(new IndexConfig(IndexType.HASH, HazelcastIndexedSessionRepository.PRINCIPAL_NAME_ATTRIBUTE));

        SerializerConfig serializerConfig = new SerializerConfig();
        serializerConfig.setImplementation(new HazelcastSessionSerializer()).setTypeClass(MapSession.class);
        config.getSerializationConfig().addSerializerConfig(serializerConfig);

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
