package com.agonyforge.mud.core.config;

import com.agonyforge.mud.core.service.SessionAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.config.StompBrokerRelayRegistration;
import org.springframework.messaging.simp.stomp.StompDecoder;
import org.springframework.messaging.simp.stomp.StompReactorNettyCodec;
import org.springframework.messaging.tcp.TcpOperations;
import org.springframework.messaging.tcp.reactor.ReactorNettyCodec;
import org.springframework.messaging.tcp.reactor.ReactorNettyTcpClient;
import org.springframework.session.Session;
import org.springframework.session.web.socket.config.annotation.AbstractSessionWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketBrokerConfiguration extends AbstractSessionWebSocketMessageBrokerConfigurer<Session> {
    private final ActiveMqBrokerProperties brokerProperties;

    @Autowired
    public WebSocketBrokerConfiguration(ActiveMqBrokerProperties brokerProperties) {
        this.brokerProperties = brokerProperties;
    }

    @Bean
    protected SessionAttributeService getSessionAttributeService() {
        return new SessionAttributeService();
    }

    @Override
    protected void configureStompEndpoints(StompEndpointRegistry registry) {
        HttpSessionHandshakeInterceptor httpSessionHandshakeInterceptor = new HttpSessionHandshakeInterceptor();

        httpSessionHandshakeInterceptor.setCreateSession(true);
        httpSessionHandshakeInterceptor.setCopyHttpSessionId(true);
        httpSessionHandshakeInterceptor.setCopyAllAttributes(true);

        registry
            .addEndpoint("/mud")
            .setHandshakeHandler(new SessionIdUpdatingHandshakeHandler())
            .withSockJS()
            .setInterceptors(
                httpSessionHandshakeInterceptor,
                new SessionIdCopyingHandshakeInterceptor(),
                new RemoteIpHandshakeInterceptor());
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(getSessionAttributeService());
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        StompBrokerRelayRegistration relayRegistration = registry
            .setApplicationDestinationPrefixes("/app", "/user")
            .setUserDestinationPrefix("/user")
            .enableStompBrokerRelay("/queue", "/topic")
            .setUserDestinationBroadcast("/topic/user-destination")
            .setUserRegistryBroadcast("/topic/user-registry")
            .setRelayHost(brokerProperties.getHost())
            .setRelayPort(brokerProperties.getPort())
            .setSystemLogin(brokerProperties.getSystemUsername())
            .setSystemPasscode(brokerProperties.getSystemPassword())
            .setClientLogin(brokerProperties.getClientUsername())
            .setClientPasscode(brokerProperties.getClientPassword());

        if (brokerProperties.getSsl()) {
            relayRegistration.setTcpClient(createSslTcpClient());
        }
    }

    // TODO to add custom headers to outbound messages
    // https://stackoverflow.com/questions/42166472/how-to-add-custom-headers-to-stomp-created-message-in-spring-boot-application

    private TcpOperations<byte[]> createSslTcpClient() {
        StompDecoder decoder = new StompDecoder();
        ReactorNettyCodec<byte[]> codec = new StompReactorNettyCodec(decoder);

        return new ReactorNettyTcpClient<>((builder) -> builder
                .host(brokerProperties.getHost())
                .port(brokerProperties.getPort())
                .secure(),
            codec
        );
    }
}
