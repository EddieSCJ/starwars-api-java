package com.api.starwars.planet.model.domain;

import com.api.starwars.planet.model.mongo.MongoPlanet;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Planet {

    public String id;
    private final String name;
    private final String climate;
    private final String terrain;

    public Planet(String name, String climate, String terrain) {
        this.name = name;
        this.climate = climate;
        this.terrain = terrain;
    }


}
