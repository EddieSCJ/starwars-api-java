package com.api.starwars.planets.domain.operations;


import com.api.starwars.planets.domain.model.Planet;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PlanetOperations {

    Flux<Planet> findAll(Integer page, Integer size);

    Mono<Planet> findById(final String id, final Long cacheInDays);

    Mono<Planet> findByName(final String name, final Long cacheInDays);

    Mono<Planet> updateById(final String id, final Planet planet);

    Mono<Planet> save(final Planet planet);

    Mono<Planet> deleteById(final String id);

}