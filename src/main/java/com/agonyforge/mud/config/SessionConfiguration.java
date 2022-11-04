package com.agonyforge.mud.config;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.hazelcast.config.annotation.web.http.EnableHazelcastHttpSession;

@Configuration
@EnableHazelcastHttpSession
public class SessionConfiguration {
    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();

        config.setClusterName("mud-sessions");

        return Hazelcast.newHazelcastInstance(config);
    }
}
