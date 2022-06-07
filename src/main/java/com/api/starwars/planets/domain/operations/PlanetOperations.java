package com.api.starwars.planets.domain.operations;


import com.api.starwars.planets.domain.model.Planet;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PlanetOperations {

    Page<Planet> findAll(Integer page, String order, String direction, Integer size);

    Planet findById(String id, Long cacheInDays);

    Planet findByName(String name, Long cacheInDays);

    Planet updateById(String id, Planet planet);

    Planet save(Planet planet);

    List<Planet> saveAll(List<Planet> planets);

    void deleteById(String id);

}