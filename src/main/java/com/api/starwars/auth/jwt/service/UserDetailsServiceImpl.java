package com.api.starwars.auth.jwt.service;

import com.api.starwars.auth.jwt.model.mongo.MongoUser;
import com.api.starwars.auth.jwt.service.interfaces.ApplicationUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import static java.text.MessageFormat.format;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final ApplicationUserRepository applicationUserRepository;

    @Autowired
    public UserDetailsServiceImpl(ApplicationUserRepository applicationUserRepository) {
        this.applicationUserRepository = applicationUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info(format("Iniciando busca por usuario no banco. username: {0}", username));
        MongoUser user = applicationUserRepository.findByUsername(username.toLowerCase());
        if (user == null) {
            log.warn(format("Username nao encontrado. username: {0}", username));
            throw new UsernameNotFoundException(username);
        }
        Set<GrantedAuthority> authorities = user.getAuthorities().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());

        log.warn(format("Usuario encontrado com sucesso. username: {0}", username));
        return new User(user.getUsername(), user.getPassword(), authorities);
    }
}