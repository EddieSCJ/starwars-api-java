package com.api.starwars.commons.auth.jwt.model.mongo;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collection;

@Data
@Document("user")
public class MongoUser {
    @Indexed(unique = true)
    private String username;
    private String password;
    private Collection<String> authorities;
}
