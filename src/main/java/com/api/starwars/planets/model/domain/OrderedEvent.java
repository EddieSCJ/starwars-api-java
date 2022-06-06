package com.api.starwars.planets.model.domain;

/**
 * Ordered Events are made in case we need maintain sending message order
 * Once we need it, the key parameter is the key of the process to maintain
 * Because it make all messages go to same partition
 */
public record OrderedEvent(String key, Event event) {}
