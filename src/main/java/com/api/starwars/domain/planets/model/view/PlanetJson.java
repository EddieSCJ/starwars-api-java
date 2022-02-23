package com.api.starwars.domain.planets.model.view;

import com.api.starwars.domain.planets.model.domain.Planet;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class PlanetJson {
    private final String id;
    private final String name;
    private final String[] climate;
    private final String[] terrain;
    private final Long cacheInDays;
    private final Integer movieAppearances;

    public static PlanetJson fromDomain(Planet planet) {
        return new PlanetJson(
                planet.id(),
                planet.name(),
                planet.climate(),
                planet.terrain(),
                planet.cacheInDays(),
                planet.movieAppearances()
        );
    }

    public Planet toDomain() {
        return new Planet(
                this.id,
                this.name,
                this.climate,
                this.terrain,
                this.movieAppearances,
                this.cacheInDays
        );
    }

}
