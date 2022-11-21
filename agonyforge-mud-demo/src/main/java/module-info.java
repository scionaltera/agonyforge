module com.agonyforge.mud.demo {
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.beans;
    requires spring.security.core;
    requires spring.security.web;
    requires spring.session.core;
    requires com.agonyforge.mud.core;
    requires org.slf4j;
    requires mud.agonyforge.mud.dynamodb;
    requires spring.security.oauth2.core;
    requires spring.messaging;
    requires spring.websocket;
}
