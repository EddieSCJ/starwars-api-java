package com.api.starwars.planet.mappers.view;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlanetResponseJson {

    private final Integer statusCode;
    private final PlanetResponseBodyJson planetResponseBodyJson;

    public PlanetResponseJson(Integer statusCode, PlanetResponseBodyJson planetResponseBodyJson) {
        this.statusCode = statusCode;
        this.planetResponseBodyJson = planetResponseBodyJson;
    }

}
