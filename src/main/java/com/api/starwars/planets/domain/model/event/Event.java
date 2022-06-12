package com.api.starwars.planets.domain.model.event;

public record Event(String type, EventEnum event, String planetName) { }
