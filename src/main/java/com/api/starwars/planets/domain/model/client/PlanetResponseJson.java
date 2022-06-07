package com.api.starwars.planets.domain.model.client;

import lombok.Data;

import java.util.List;

@Data
public class PlanetResponseJson {
    private Integer count;
    private List<PlanetJson> results;
}
