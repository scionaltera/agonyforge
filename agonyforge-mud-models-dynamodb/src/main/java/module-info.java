module com.agonyforge.mud.models.dynamodb {
    exports com.agonyforge.mud.models.dynamodb;
    exports com.agonyforge.mud.models.dynamodb.config;
    exports com.agonyforge.mud.models.dynamodb.repository;
    exports com.agonyforge.mud.models.dynamodb.impl;
    exports com.agonyforge.mud.models.dynamodb.service;
    exports com.agonyforge.mud.models.dynamodb.constant;
    requires org.slf4j;
    requires software.amazon.awssdk.services.dynamodb;
    requires spring.context;
    requires software.amazon.awssdk.regions;
    requires spring.beans;
    requires spring.boot;
    requires com.agonyforge.mud.core;
    requires spring.messaging;
}
