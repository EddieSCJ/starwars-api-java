package com.api.starwars.planets.mappers.view;

import lombok.Data;

import java.util.List;

@Data
public class PlanetResponseJson {
    private Integer count;
    private List<MPlanetJson> results;
}
