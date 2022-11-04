module com.agonyforge.mud.modules.application {
    requires spring.beans;
    requires spring.boot;
    requires spring.context;
    requires spring.security.web;
    requires spring.web;
    requires org.apache.tomcat.embed.core;
    requires spring.websocket;
    requires spring.session.core;
    requires spring.messaging;
    requires reactor.netty.core;
    requires org.slf4j;
    requires spring.security.config;
    requires spring.security.core;
    requires com.hazelcast.core;
    requires spring.session.hazelcast;
}