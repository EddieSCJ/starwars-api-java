package com.api.starwars.auth.jwt.app.storage.mongo;

import com.api.starwars.auth.jwt.app.storage.mongo.model.MongoUser;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ApplicationUserRepository extends MongoRepository<MongoUser, String> {
    MongoUser findByUsername(String username);
}
