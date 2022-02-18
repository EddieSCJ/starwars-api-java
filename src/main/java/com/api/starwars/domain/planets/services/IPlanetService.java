package com.api.starwars.domain.planets.services;


import com.api.starwars.commons.exceptions.http.HttpNotFoundException;
import com.api.starwars.domain.planets.model.domain.Planet;
import org.springframework.data.domain.Page;

import javax.naming.ServiceUnavailableException;
import java.util.List;

public interface IPlanetService {

    Page<Planet> findAll(Integer page, String order, String direction, Integer size) throws Exception;

    Planet findById(String id, Long cacheInDays) throws Exception;

    Planet findByName(String name, Long cacheInDays) throws Exception;

    Planet save(Planet planet);

    List<Planet> saveAll(List<Planet> planets);

    void deleteById(String id);

    List<Planet> findAllFromStarWarsApi() throws Exception;

    List<PlanetJson> planetsToPlanetJson(List<Planet> planets) throws ServiceUnavailableException;


}