package com.api.starwars.domain.planets.repositories;

import com.api.starwars.domain.planets.model.mongo.MongoPlanet;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IPlanetMongoRepository extends MongoRepository<MongoPlanet, String> {}
