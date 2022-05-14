package com.api.starwars.planets.services.interfaces;

import com.api.starwars.planets.model.mongo.MongoPlanet;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IPlanetMongoRepository extends MongoRepository<MongoPlanet, String> {}
