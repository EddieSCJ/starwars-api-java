package com.api.starwars.planets.model.domain;

public record Planet(String id, String name, String[] climate, String[] terrain, Integer movieAppearances,
                     Long cacheInDays) {
}

