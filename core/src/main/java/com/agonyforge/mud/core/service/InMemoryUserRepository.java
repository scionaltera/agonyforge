package com.agonyforge.mud.core.service;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class InMemoryUserRepository {
    private final Set<String> wsSessionNames = new HashSet<>();

    public Set<String> getWsSessionNames() {
        return wsSessionNames;
    }
}
