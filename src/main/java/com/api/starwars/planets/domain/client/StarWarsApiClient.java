package com.api.starwars.planets.domain.client;

import com.api.starwars.planets.domain.model.client.PlanetResponseJson;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

import static com.api.starwars.planets.app.handler.Constants.PLANETS_ENDPOINT;

@FeignClient(name = "${clients.starwars.name}", url = "${clients.starwars.url}")
public interface StarWarsApiClient {

    @GetMapping(value = PLANETS_ENDPOINT + "/")
    PlanetResponseJson getPlanetBy(@RequestParam("search") String name) throws IOException, InterruptedException;

    @GetMapping(value = PLANETS_ENDPOINT)
    PlanetResponseJson getPlanets();
}
