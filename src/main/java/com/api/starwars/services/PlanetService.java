package com.api.starwars.services;

import com.api.starwars.domain.Planet;
import com.api.starwars.domain.dtos.PlanetDTO;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface PlanetService {

    Integer getMoviesAppearancesQuantity(String planetName) throws IOException, InterruptedException;

    List<PlanetDTO> getAll() throws IOException, InterruptedException;

    Page<PlanetDTO> getAllPaginated(Integer page, String order, String direction) throws IOException, InterruptedException;

    PlanetDTO findById(String id) throws IOException, InterruptedException;

    PlanetDTO findByName(String name) throws IOException, InterruptedException;

    Planet save(Planet planet);

    void deleteById(String id);

    Map<String, Object> alreadyExists(String name, String id);

}
