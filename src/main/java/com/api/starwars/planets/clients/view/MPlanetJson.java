package com.api.starwars.planets.clients.view;

import com.api.starwars.planets.model.domain.Planet;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MPlanetJson {
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
