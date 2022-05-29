package com.api.starwars.auth.jwt.service.interfaces;

import com.api.starwars.auth.jwt.model.mongo.MongoUser;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ApplicationUserRepository extends MongoRepository<MongoUser, String> {
    MongoUser findByUsername(String username);
}
