module com.agonyforge.mud.demo {
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.beans;
    requires spring.security.core;
    requires spring.security.web;
    requires spring.session.core;
    requires com.agonyforge.mud.core;
    exports com.agonyforge.mud.demo;
}