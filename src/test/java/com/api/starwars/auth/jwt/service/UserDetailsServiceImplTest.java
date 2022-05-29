package com.api.starwars.auth.jwt.service;

import com.api.starwars.auth.jwt.model.mongo.MongoUser;
import com.api.starwars.auth.jwt.service.interfaces.ApplicationUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class UserDetailsServiceImplTest {

    @Mock
    ApplicationUserRepository applicationUserRepository;

    @InjectMocks
    UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve carregar por username corretamente")
    void test_load_by_username() {
        String username = "user";
        when(applicationUserRepository.findByUsername(username)).thenReturn(new MongoUser(username, "123", List.of("PLANET_CREATE", "PLANET_DELETE")));

        UserDetails user = userDetailsService.loadUserByUsername(username);
        assertEquals(username, user.getUsername());
        assertEquals("123", user.getPassword() );
        assertTrue(user.getAuthorities().size() > 0);
    }
}
