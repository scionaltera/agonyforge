package com.agonyforge.mud.demo.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal == null) {
            return Optional.empty();
        }

        return Optional.of(((AuthenticatedPrincipal) principal).getName());
    }
}
