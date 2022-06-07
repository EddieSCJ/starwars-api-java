package com.api.starwars.planets.app.storage.mongo;

import com.api.starwars.planets.app.storage.mongo.model.MongoPlanet;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IPlanetMongoRepository extends MongoRepository<MongoPlanet, String> {}
