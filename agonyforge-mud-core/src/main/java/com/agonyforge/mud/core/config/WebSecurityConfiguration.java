package com.agonyforge.mud.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {
    public WebSecurityConfiguration() {}

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler csrfRequestHandler = new CsrfTokenRequestAttributeHandler();
        http
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers(
                    "/",
                    "/error",
                    "/img/*",
                    "/css/*",
                    "/js/*",
                    "/webjars/**",
                    "/csrf"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .csrf((csrf) -> csrf.csrfTokenRequestHandler(csrfRequestHandler))
            .oauth2Login((oauth) -> oauth
                .loginPage("/")
                .defaultSuccessUrl("/play"))
            .logout((logout) -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
            )
            .sessionManagement((sessionMgmt) -> sessionMgmt
                .invalidSessionUrl("/")
                .maximumSessions(1)
                .maxSessionsPreventsLogin(true)
                .expiredUrl("/")
                .sessionRegistry(sessionRegistry())
            );

        return http.build();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
}
