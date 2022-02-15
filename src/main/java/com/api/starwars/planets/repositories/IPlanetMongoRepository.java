package com.api.starwars.planets.repositories;

import com.api.starwars.planets.model.mongo.MongoPlanet;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IPlanetMongoRepository extends MongoRepository<MongoPlanet, String> {}
