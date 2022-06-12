package com.api.starwars.planets.app.storage.mongo;

import com.api.starwars.planets.app.storage.mongo.model.MongoPlanet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

public interface IPlanetMongoRepository extends ReactiveSortingRepository<MongoPlanet, String> {
    Flux<MongoPlanet> findAllByIdNotNull(final Pageable page);
}
