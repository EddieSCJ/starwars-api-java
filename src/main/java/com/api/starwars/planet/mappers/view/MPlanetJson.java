package com.api.starwars.planet.mappers.view;

import com.api.starwars.planet.model.domain.Planet;

import java.util.List;

public record MPlanetJson(String name, String climate, String terrain, List<String> films) {

    public Planet toDomain() {
        return new Planet(
                null,
                this.name,
                this.climate,
                this.terrain,
                this.films.size(),
                0L
        );
    }
}
