package com.api.starwars.planet.repositories.planets;


import com.api.starwars.planet.model.domain.Planet;
import com.api.starwars.planet.model.mongo.MongoPlanet;

import java.util.List;
import java.util.Optional;

public interface IPlanetRepository  {

    Optional<MongoPlanet> findByName(String name);

    Optional<MongoPlanet> findbyId(String id);

    MongoPlanet save(Planet planet);

    void deleteById(String id);
}
