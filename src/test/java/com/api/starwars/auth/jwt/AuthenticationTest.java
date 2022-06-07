package com.api.starwars.auth.jwt;

import com.api.starwars.auth.jwt.app.storage.mongo.ApplicationUserRepository;
import com.api.starwars.auth.jwt.app.storage.mongo.model.MongoUser;
import com.api.starwars.auth.jwt.domain.ApplicationUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import commons.base.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

class AuthenticationTest extends AbstractIntegrationTest {

    private static final MongoUser APPLICATION_USER = new MongoUser(
            "another_application_who_consumes_this_api",
            "$2a$10$W1NqIc3gvpLNwQska2iAFOQ1FOpUDQ1a5FF.ffAF2eUNuaLrr3FKm",
            List.of("PLANET_LIST", "PLANET_CREATE", "PLANET_UPDATE", "PLANET_DELETE")
    );

    private static final Gson GSON = new GsonBuilder().create();

    @Autowired
    private ApplicationUserRepository applicationUserRepository;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void insertData() {
        applicationUserRepository.deleteAll();
        applicationUserRepository.save(APPLICATION_USER);
    }

    @Test
    @DisplayName("Deve autenticar com sucesso")
    void shouldAuthenticate() throws Exception {
        String userJSON = GSON.toJson(new ApplicationUser("another_application_who_consumes_this_api", "12", Collections.emptyList())); // I am using the GSON lib provided by google to

        MvcResult mvcResult = mockMvc.perform(post("/login").content(userJSON)).andReturn();
        String token = mvcResult.getResponse().getContentAsString();
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("Deve falhar ao autenticar em razão de senha incorreta")
    void shouldNotAuthenticate() throws Exception {
        String userJSON = GSON.toJson(new ApplicationUser("another_application_who_consumes_this_api", "123", Collections.emptyList())); // I am using the GSON lib provided by google to

        MvcResult mvcResult = mockMvc.perform(post("/login").content(userJSON)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(HttpStatus.UNAUTHORIZED.value(), status);
    }

}