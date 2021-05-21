package com.api.starwars.repositories;

import com.api.starwars.domain.Planet;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlanetRepository extends MongoRepository<Planet, String> {

    Planet findByName(String name);

    Planet findByNameOrId(String name, String id);

}
