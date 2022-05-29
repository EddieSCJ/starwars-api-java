package com.api.starwars.planets.services.interfaces;


import com.api.starwars.planets.model.domain.Planet;

import java.util.Optional;

public interface IPlanetRepository  {
    Long count();
    Optional<Planet> findByName(String name);
    Optional<Planet> findById(String id);
    Planet save(Planet planet);
    void deleteById(String id);
}
