package com.api.starwars.commons.auth.jwt.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
@AllArgsConstructor
public class ApplicationUser {
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
}
