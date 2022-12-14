module com.agonyforge.mud.core {
    exports com.agonyforge.mud.core.cli;
    exports com.agonyforge.mud.core.cli.menu;
    exports com.agonyforge.mud.core.web.model;
    exports com.agonyforge.mud.core.service;
    exports com.agonyforge.mud.core.web.controller;
    exports com.agonyforge.mud.core.config;
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
    requires spring.security.oauth2.core;
    requires spring.security.oauth2.client;
    requires spring.core;
    requires spring.webmvc;
}
