package com.api.starwars.planets.domain.storage;


import com.api.starwars.planets.domain.model.Planet;

import java.util.Optional;

public interface PlanetStorage {
    Long count();
    Optional<Planet> findByName(String name);
    Optional<Planet> findById(String id);
    Planet save(Planet planet);
    void deleteById(String id);
}
