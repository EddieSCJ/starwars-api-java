package com.api.starwars.domain.planets.services;


import com.api.starwars.domain.planets.model.domain.Planet;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;

public interface IPlanetService {

    Page<Planet> findAll(Integer page, String order, String direction, Integer size);

    Planet findById(String id, Long cacheInDays) throws IOException, InterruptedException;

    Planet findByName(String name, Long cacheInDays) throws IOException, InterruptedException;

    Planet updateById(String id, Planet planet);

    Planet save(Planet planet);

    List<Planet> saveAll(List<Planet> planets);

    void deleteById(String id);

}