package com.api.starwars.planet.mappers.view;

import lombok.Builder;
import lombok.Data;

public record PlanetResponseJson(Integer statusCode, PlanetResponseBodyJson planetResponseBodyJson) {}
