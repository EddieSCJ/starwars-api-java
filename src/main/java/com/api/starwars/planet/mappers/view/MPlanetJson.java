package com.api.starwars.planet.mappers.view;

import com.api.starwars.planet.model.domain.Planet;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MPlanetJson {
    private final String name;
    private final String climate;
    private final String terrain;
    private final List<String> films;

    public Planet toDomain() {
       return Planet.builder()
                .name(this.name)
                .climate(this.climate)
                .terrain(this.terrain)
                .movieAppeareces(this.films.size())
                .build();
    }
}
