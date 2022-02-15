package com.api.starwars.planets.mappers.view;

import com.api.starwars.planets.model.domain.Planet;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MPlanetJson {
    private String name;
    private String climate;
    private String terrain;
    private List<String> films = new ArrayList<>();

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
