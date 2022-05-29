package com.api.starwars.auth.jwt.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collection;

@Data
@AllArgsConstructor
@Document("user")
public class MongoUser {
    @Indexed(unique = true)
    private String username;
    private String password;
    private Collection<String> authorities;
}
