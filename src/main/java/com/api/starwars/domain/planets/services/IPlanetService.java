package com.api.starwars.domain.planets.services;


import com.api.starwars.domain.planets.model.domain.Planet;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;

public interface IPlanetService {

    Page<Planet> findAll(Integer page, String order, String direction, Integer size);

    Planet findById(String id, Long cacheInDays) throws IOException, InterruptedException;

    Planet findByName(String name, Long cacheInDays) throws IOException, InterruptedException;

    Planet save(Planet planet);

    Planet updateById(String id, Planet planet);

    List<Planet> saveAll(List<Planet> planets);

    void deleteById(String id);

    List<Planet> findAllFromStarWarsApi();

}