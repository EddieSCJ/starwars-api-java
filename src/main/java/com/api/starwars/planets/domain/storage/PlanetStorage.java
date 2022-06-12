package com.api.starwars.planets.domain.storage;


import com.api.starwars.planets.domain.model.Planet;
import reactor.core.publisher.Mono;

public interface PlanetStorage {
    Mono<Long> count();
    Mono<Planet> findByName(final String name);
    Mono<Planet> findById(final String id);
    Mono<Planet> save(final Mono<Planet> planet);
    Mono<Planet> deleteById(final String id);
}
