package com.api.starwars.planet.mappers.view;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PlanetJson {
    private final List<String> films;
}
