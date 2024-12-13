package com.agonyforge.mud.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.support.converter.PassThruMessageConverter;
import org.springframework.messaging.simp.stomp.ReactorNettyTcpStompClient;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableIntegration
public class StompClientConfiguration {
    private final MqBrokerProperties brokerProperties;

    @Autowired
    public StompClientConfiguration(MqBrokerProperties brokerProperties) {
        this.brokerProperties = brokerProperties;
    }

    @Bean
    public ReactorNettyTcpStompClient stompClient() {
        ReactorNettyTcpStompClient stompClient = new ReactorNettyTcpStompClient(brokerProperties.getHost(), brokerProperties.getPort());
        stompClient.setMessageConverter(new PassThruMessageConverter());
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.afterPropertiesSet();
        stompClient.setTaskScheduler(taskScheduler);
        stompClient.setReceiptTimeLimit(5000);

        return stompClient;
    }
}
