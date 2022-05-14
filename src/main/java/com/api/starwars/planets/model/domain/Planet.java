package com.api.starwars.planets.model.domain;

import java.util.Arrays;
import java.util.Objects;

public record Planet(String id, String name, String[] climate, String[] terrain, Integer movieAppearances, Long cacheInDays) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Planet planet = (Planet) o;
        return Objects.equals(id, planet.id) && Objects.equals(name, planet.name) && Arrays.equals(climate, planet.climate) && Arrays.equals(terrain, planet.terrain) && Objects.equals(movieAppearances, planet.movieAppearances) && Objects.equals(cacheInDays, planet.cacheInDays);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, name, movieAppearances, cacheInDays);
        result = 31 * result + Arrays.hashCode(climate);
        result = 31 * result + Arrays.hashCode(terrain);
        return result;
    }
}