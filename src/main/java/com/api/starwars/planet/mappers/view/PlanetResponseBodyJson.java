package com.api.starwars.planet.mappers.view;

import java.util.List;

public record PlanetResponseBodyJson(Integer count, List<MPlanetJson> results) {
}
