package com.api.starwars.domain.planets.model.view;

import com.api.starwars.domain.planets.model.domain.Planet;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.api.starwars.commons.helpers.ErrorMessageHelper.notEmpty;
import static com.api.starwars.commons.helpers.ErrorMessageHelper.notNull;

@Data
@AllArgsConstructor
public class PlanetJson {
    private final String id;

    @NotEmpty(message = "The name " + notEmpty)
    @NotNull(message = "The name " + notNull)
    private final String name;

    @NotEmpty(message = "The climate " + notEmpty)
    @NotNull(message = "The climate " + notNull)
    private final String[] climate;

    @NotEmpty(message = "The terrain " + notEmpty)
    @NotNull(message = "The Terrain " + notNull)
    private final String[] terrain;

    @NotNull(message = "The cache in days " + notNull)
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
