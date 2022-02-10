package com.api.starwars.planet.repositories;


import com.api.starwars.planet.model.domain.Planet;
import com.api.starwars.planet.model.mongo.MongoPlanet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IPlanetRepository  {

    Long count();

    Optional<MongoPlanet> findByName(String name);

    Optional<MongoPlanet> findbyId(String id);

    MongoPlanet save(Planet planet);

    void deleteById(String id);
}
