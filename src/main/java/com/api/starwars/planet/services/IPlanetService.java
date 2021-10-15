package com.api.starwars.planet.services;



import com.api.starwars.planet.model.domain.Planet;
import com.api.starwars.planet.model.view.Planet;
import com.api.starwars.planet.model.view.PlanetJson;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IPlanetService {

    Integer getMoviesAppearancesQuantity(String planetName) throws IOException, InterruptedException;

    List<Planet> findAll() throws IOException, InterruptedException;

    Page<PlanetJson> findAll(Integer page, String order, String direction) throws IOException, InterruptedException;

    Optional<Planet> findById(String id) throws IOException, InterruptedException;

    Optional<Planet> findByName(String name) throws IOException, InterruptedException;

    Planet save(Planet planet);

    void deleteById(String id);

    Optional<Planet> alreadyExists(String name, String id);

}
