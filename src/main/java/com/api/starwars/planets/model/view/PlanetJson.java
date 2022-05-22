package com.api.starwars.planets.model.view;

import com.api.starwars.planets.model.domain.Planet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class PlanetJson extends RepresentationModel<PlanetJson> {
    private String id;
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
