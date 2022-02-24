package com.api.starwars.domain.planets.clients.view;

import lombok.Data;

import java.util.List;

@Data
public class PlanetResponseJson {
    private Integer count;
    private List<MPlanetJson> results;
}
