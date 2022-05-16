package com.api.starwars.commons.auth.jwt.model.domain;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
public class ApplicationUser {
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
}
