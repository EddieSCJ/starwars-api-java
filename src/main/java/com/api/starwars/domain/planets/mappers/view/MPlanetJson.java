package com.api.starwars.domain.planets.mappers.view;

import com.api.starwars.domain.planets.model.domain.Planet;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MPlanetJson {
    private String name;
    private String climate;
    private String terrain;
    private List<String> films = new ArrayList<>();

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
