package com.api.starwars.planets.clients.view;

import lombok.Data;

import java.util.List;

@Data
public class PlanetResponseJson {
    private Integer count;
    private List<MPlanetJson> results;
}
