package com.api.starwars.planet.model.view;

import com.api.starwars.planet.model.domain.Planet;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.api.commons.helpers.ErrorMessageHelper.notEmpty;
import static com.api.commons.helpers.ErrorMessageHelper.notNull;

@Data
public class PlanetJson {
    private final String id;

    @NotEmpty(message = "The name " + notEmpty)
    @NotNull(message = "The name " + notNull)
    private final String name;

    @NotEmpty(message = "The climate " + notEmpty)
    @NotNull(message = "The climate " + notNull)
    private final String climate;

    @NotEmpty(message = "The terrain " + notEmpty)
    @NotNull(message = "The Terrain " + notNull)
    private final String terrain;

    @NotNull(message = "The cache in days " + notNull)
    private final Long _cacheInDays;
    private final Integer _movieAppearances;

    public PlanetJson(String id, String name, String climate, String terrain, Long _cacheInDays, Integer _movieAppearances) {
        this.id = id;
        this.name = name;
        this.climate = climate;
        this.terrain = terrain;
        this._cacheInDays = _cacheInDays;
        this._movieAppearances = _movieAppearances;
    }

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
                this._movieAppearances,
                this._cacheInDays
        );
    }

}
