package com.api.starwars.planet.model.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Planet {

    private final String id;
    private final String name;
    private final String climate;
    private final String terrain;
    private final Integer movieAppeareces;
    private final Long cacheInDays;

    public Planet(String id, String name, String climate, String terrain, Integer movieAppeareces, Long cacheInDays) {
        this.id = id;
        this.name = name;
        this.climate = climate;
        this.terrain = terrain;
        this.movieAppeareces = movieAppeareces;
        this.cacheInDays = cacheInDays;
    }


}
