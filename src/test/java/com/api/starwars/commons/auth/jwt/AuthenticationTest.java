package com.api.starwars.commons.auth.jwt;

import com.api.starwars.commons.auth.jwt.model.domain.ApplicationUser;
import com.api.starwars.commons.auth.jwt.model.mongo.MongoUser;
import com.api.starwars.commons.auth.jwt.service.interfaces.ApplicationUserRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import commons.base.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
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

public class AuthenticationTest extends AbstractIntegrationTest {

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
    public void insertData() {
        applicationUserRepository.deleteAll();
        applicationUserRepository.save(APPLICATION_USER);
    }

    @Test
    public void shouldAuthenticate() throws Exception {
        String userJSON = GSON.toJson(new ApplicationUser("another_application_who_consumes_this_api", "12", Collections.emptyList())); // I am using the GSON lib provided by google to

        MvcResult mvcResult = mockMvc.perform(post("/login").content(userJSON)).andReturn();
        String token = mvcResult.getResponse().getContentAsString();
        assertFalse(token.isEmpty());
    }

    @Test
    public void shouldNotAuthenticate() throws Exception {
        String userJSON = GSON.toJson(new ApplicationUser("another_application_who_consumes_this_api", "123", Collections.emptyList())); // I am using the GSON lib provided by google to

        MvcResult mvcResult = mockMvc.perform(post("/login").content(userJSON)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(HttpStatus.UNAUTHORIZED.value(), status);
    }

}