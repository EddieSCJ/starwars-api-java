package com.api.starwars.planet.mappers.view;

import lombok.Data;

import java.util.List;

@Data
public class PlanetResponseBodyJson {
    private Integer count;
    private List<MPlanetJson> results;
}
