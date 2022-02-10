package com.api.starwars.planet.services;


import com.api.starwars.planet.model.domain.Planet;
import com.api.starwars.planet.model.mongo.MongoPlanet;
import com.api.starwars.planet.model.view.PlanetJson;
import org.springframework.data.domain.Page;

import javax.naming.ServiceUnavailableException;
import java.io.IOException;
import java.util.List;

public interface IPlanetService {

    Page<Planet> findAll(Integer page, String order, String direction, Integer size) throws IOException, InterruptedException;

    Planet findById(String id, Long cacheInDays) throws Exception;

    Planet findByName(String name, Long cacheInDays) throws Exception;

    MongoPlanet save(Planet planet);

    List<MongoPlanet> saveAll(List<Planet> planets);

    void deleteById(String id);

    List<PlanetJson> planetsToPlanetJson(List<Planet> planets) throws ServiceUnavailableException;

    List<Planet> findAllFromStarWarsApi() throws IOException, InterruptedException;

}