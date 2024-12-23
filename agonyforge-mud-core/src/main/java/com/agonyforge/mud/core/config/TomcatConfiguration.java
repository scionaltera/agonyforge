package com.agonyforge.mud.core.config;

import ch.qos.logback.access.tomcat.LogbackValve;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

/**
 * The LogbackValve hooks up to the underlying Tomcat server and pipes logs out of it.
 * It reads logback-access.xml for configuration, which by default sends them to our
 * custom AccessLogAppender, which sends them into the regular MUD logs. In games that
 * have a great deal of HTTP traffic this could cause performance slowdown. In that case
 * you can remove the LogbackValve here. The logs will still be available in files inside
 * the docker container.
 */
@Configuration
public class TomcatConfiguration implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        LogbackValve valve = new LogbackValve();
        valve.setQuiet(false);
        valve.setAsyncSupported(true);

        factory.addEngineValves(valve);
        factory.addContextValves(valve);
    }
}
