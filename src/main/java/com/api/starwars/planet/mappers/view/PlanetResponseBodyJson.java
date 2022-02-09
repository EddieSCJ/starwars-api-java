package com.api.starwars.planet.mappers.view;

import lombok.Builder;
import lombok.Data;

import java.util.List;

public record PlanetResponseBodyJson(Integer count, List<MPlanetJson> results) {}
