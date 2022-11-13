module mud.agonyforge.mud.dynamodb {
    exports com.agonyforge.mud.models.dynamodb;
    exports com.agonyforge.mud.models.dynamodb.repository;
    exports com.agonyforge.mud.models.dynamodb.impl;
    requires org.slf4j;
    requires software.amazon.awssdk.services.dynamodb;
    requires spring.context;
    requires software.amazon.awssdk.regions;
    requires spring.beans;
}
