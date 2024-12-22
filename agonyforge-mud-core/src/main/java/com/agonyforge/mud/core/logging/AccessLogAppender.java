package com.agonyforge.mud.core.logging;

import ch.qos.logback.access.common.PatternLayoutEncoder;
import ch.qos.logback.access.common.spi.IAccessEvent;
import ch.qos.logback.core.AppenderBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public class AccessLogAppender extends AppenderBase<IAccessEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccessLogAppender.class);

    PatternLayoutEncoder encoder;

    @Override
    public void start() {
        if (this.encoder == null) {
            addError(String.format("No encoder set for the appender named [%s]", name));
            return;
        }

        encoder.start();
        super.start();
    }

    public void append(IAccessEvent event) {
        LOGGER.debug("Access Log: {}", new String(encoder.encode(event), Charset.defaultCharset()).trim());
    }

    public PatternLayoutEncoder getEncoder() {
        return encoder;
    }

    public void setEncoder(PatternLayoutEncoder encoder) {
        this.encoder = encoder;
    }
}
