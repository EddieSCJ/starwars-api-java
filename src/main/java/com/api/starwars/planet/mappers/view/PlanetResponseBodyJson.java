package com.api.starwars.planet.mappers.view;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PlanetResponseBodyJson {

    private final Integer count;
    private final List<PlanetJson> results;


}
