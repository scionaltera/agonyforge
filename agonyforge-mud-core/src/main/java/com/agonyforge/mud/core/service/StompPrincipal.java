package com.agonyforge.mud.core.service;

import java.security.Principal;

/*
 * Very simple Principal that just holds a name.
 */
public class StompPrincipal implements Principal {
    private final String name;

    public StompPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
