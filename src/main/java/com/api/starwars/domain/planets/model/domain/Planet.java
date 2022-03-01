package com.api.starwars.domain.planets.model.domain;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public record Planet(String id, String name, String[] climate, String[] terrain, Integer movieAppearances, Long cacheInDays) {}