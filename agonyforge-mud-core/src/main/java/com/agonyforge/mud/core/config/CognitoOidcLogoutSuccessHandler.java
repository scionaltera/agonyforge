package com.agonyforge.mud.core.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * Returns a URL for Cognito's logout URL so we can hit it as part of the
 * logout process and invalidate our tokens at Amazon to fully log out.
 *
 * Borrowed from https://rieckpil.de/oidc-logout-with-aws-cognito-and-spring-security/
 */
public class CognitoOidcLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CognitoOidcLogoutSuccessHandler.class);

    private final String logoutUrl;
    private final String clientId;

    public CognitoOidcLogoutSuccessHandler(String logoutUrl, String clientId) {
        this.logoutUrl = logoutUrl;
        this.clientId = clientId;
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        LOGGER.debug("Using params: client_id={} logout_url={}", clientId, logoutUrl);

        UriComponents baseUrl = UriComponentsBuilder
            .fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
            .replacePath(request.getContextPath())
            .replaceQuery(null)
            .fragment(null)
            .build();

        String result = UriComponentsBuilder
            .fromUri(URI.create(logoutUrl))
            .queryParam("client_id", clientId)
            .queryParam("logout_uri", baseUrl)
            .encode(StandardCharsets.UTF_8)
            .build()
            .toUriString();

        LOGGER.debug("Generated logout URL for Cognito: {}", result);

        return result;
    }
}
