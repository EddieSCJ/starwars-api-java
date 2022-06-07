package com.api.starwars.auth.jwt.domain.view;

import lombok.Data;

import java.util.Collection;

@Data
public class UserJson {
    private String username;
    private String password;
    private Collection<String> authorities;
}
