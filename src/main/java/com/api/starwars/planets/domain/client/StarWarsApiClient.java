package com.api.starwars.planets.domain.client;

import com.api.starwars.planets.domain.model.client.PlanetResponseJson;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import reactor.core.publisher.Mono;

import static com.api.starwars.planets.app.handler.Constants.PLANETS_ENDPOINT;

@Headers({"Accept: application/json"})
public interface StarWarsApiClient {

    @RequestLine("GET " + PLANETS_ENDPOINT + "/?search={search}")
    Mono<PlanetResponseJson> getPlanetBy(@Param("search") String name);

    @RequestLine("GET " + PLANETS_ENDPOINT)
    Mono<PlanetResponseJson> getPlanets();
}
