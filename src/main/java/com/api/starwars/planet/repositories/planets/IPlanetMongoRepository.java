package com.api.starwars.planet.repositories.planets;

import com.api.starwars.planet.model.mongo.MongoPlanet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IPlanetMongoRepository extends MongoRepository<MongoPlanet, String> {

    Page<MongoPlanet> findAll(Pageable page);
}
