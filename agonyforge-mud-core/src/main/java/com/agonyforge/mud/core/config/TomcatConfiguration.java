package com.agonyforge.mud.core.config;

import ch.qos.logback.access.tomcat.LogbackValve;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

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
