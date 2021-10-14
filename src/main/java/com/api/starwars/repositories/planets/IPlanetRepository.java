package com.api.starwars.repositories;


import com.api.starwars.model.mongo.MongoPlanet;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IPlanetRepository extends MongoRepository<MongoPlanet, String> {

}
