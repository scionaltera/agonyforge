package com.agonyforge.mud.core.service;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/*
 * Temporary shim until I can be bothered to write the DynamoDB interface.
 * This stores a set of the websocket principal names of each connected user.
 * In the future the database will have every connected user's names attached
 * to their characters, and we will just grab the list of PCs in the room or
 * the zone or whatever we need to send messages to.
 *
 * TODO No tests because this is hopefully going away soon.
 */
@Component
public class InMemoryUserRepository {
    private final Set<String> wsSessionNames = new HashSet<>();

    public Set<String> getWsSessionNames() {
        return wsSessionNames;
    }
}
