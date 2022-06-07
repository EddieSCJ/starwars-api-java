package com.api.starwars.planets.domain.model.client;

import com.api.starwars.planets.domain.model.Planet;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PlanetJson {
    private String name;
    private String climate;
    private String terrain;
    private List<String> films;

    public Planet toDomain(String id) {
        return new Planet(
                id,
                this.name,
                this.climate.replace(" ", "").split(","),
                this.terrain.replace(" ", "").split(","),
                this.films.size(),
                0L
        );
    }
}
