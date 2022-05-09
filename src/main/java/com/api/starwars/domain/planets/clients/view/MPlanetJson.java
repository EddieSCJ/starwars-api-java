package com.api.starwars.domain.planets.clients.view;

import com.api.starwars.domain.planets.model.domain.Planet;
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
                this.climate.replaceAll(" ", "").split(","),
                this.terrain.replaceAll(" ", "").split(","),
                this.films.size(),
                0L
        );
    }
}
