package com.api.starwars.domain.planets.clients;

import com.api.starwars.domain.planets.clients.view.PlanetResponseJson;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

import static com.api.starwars.domain.planets.handlers.Constants.PLANETS_ENDPOINT;

@FeignClient(name = "${clients.starwars.name}", url = "${clients.starwars.url}")
public interface IStarWarsApiClient {

    @GetMapping(value = PLANETS_ENDPOINT + "/")
    PlanetResponseJson getPlanetBy(@RequestParam("search") String name) throws IOException, InterruptedException;

    @GetMapping(value = PLANETS_ENDPOINT)
    PlanetResponseJson getPlanets();
}
