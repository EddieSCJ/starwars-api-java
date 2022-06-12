package com.api.starwars.planets.app.services;

import com.api.starwars.common.exceptions.http.BadRequestError;
import com.api.starwars.common.exceptions.http.ConflictError;
import com.api.starwars.common.exceptions.http.NotFoundError;
import com.api.starwars.planets.app.storage.mongo.IPlanetMongoRepository;
import com.api.starwars.planets.app.storage.mongo.model.MongoPlanet;
import com.api.starwars.planets.app.validations.PlanetValidator;
import com.api.starwars.planets.domain.client.StarWarsApiClient;
import com.api.starwars.planets.domain.model.Planet;
import com.api.starwars.planets.domain.operations.PlanetOperations;
import com.api.starwars.planets.domain.storage.PlanetStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;

import static java.text.MessageFormat.format;

@Slf4j
@Service
public class PlanetService implements PlanetOperations {

    private final StarWarsApiClient starWarsApiClient;
    private final IPlanetMongoRepository planetMongoRepository;
    private final PlanetStorage planetRepository;
    private final PlanetValidator planetValidator;

    @Autowired
    public PlanetService(StarWarsApiClient starWarsApi,
                         IPlanetMongoRepository planetMongoRepository,
                         PlanetStorage planetRepository,
                         PlanetValidator planetValidator) {
        this.starWarsApiClient = starWarsApi;
        this.planetMongoRepository = planetMongoRepository;
        this.planetRepository = planetRepository;
        this.planetValidator = planetValidator;
    }

    @Override
    public Flux<Planet> findAll(Integer page, Integer size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        return planetRepository.count()
                .flatMapMany(counter -> counter == 0L
                        ? getFromStarWarsClient()
                        : planetMongoRepository.findAllByIdNotNull(PageRequest.of(page, size).withSort(sort))
                        .map(MongoPlanet::toDomain)
                )
                .switchIfEmpty(Flux.error(new NotFoundError("Nenhum planeta encontrado.")));
    }

    @Override
    public Mono<Planet> findById(String id, Long cacheInDays) {
        return planetRepository.findById(id)
                .flatMap(planet ->
                        planet.cacheInDays() > cacheInDays
                                ? planetRepository.save(findFromStarWarsClientBy(planet.name(), planet.id()))
                                : Mono.just(planet))
                .switchIfEmpty(Mono.error(new NotFoundError(format("Planeta não encontrado com o id: {0}", id))));
    }

    @Override
    public Mono<Planet> findByName(String name, Long cacheInDays) {
        return planetRepository.findByName(name)
                .flatMap(planet ->
                        planet.cacheInDays() > cacheInDays
                                ? planetRepository.save(findFromStarWarsClientBy(planet.name(), planet.id()))
                                : Mono.just(planet))
                .switchIfEmpty(planetRepository.save(findFromStarWarsClientBy(name, null)));
    }

    @Override
    public Mono<Planet> updateById(String id, Planet planet) {
        List<String> errorMessages = planetValidator.validate(planet);

        if (id == null) {
            log.warn("Erro ao atualizar planeta. id {}. name: {}.", planet.id(), planet.name());
            errorMessages.add("Campo id nao pode ser nulo.");
            throw new BadRequestError(errorMessages);
        }

        if (!errorMessages.isEmpty()) {
            log.warn("Erro ao atualizar planeta. id {}. name: {}.", planet.id(), planet.name());
            throw new BadRequestError(errorMessages);
        }

        return planetRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundError(format("Planeta não encontrado com o id: {0}", id))))
                .zipWhen(p -> {
                    Planet newPlanet = new Planet(id, planet.name(), planet.climate(), planet.terrain(), planet.movieAppearances(), null);
                    return planetRepository.save(Mono.just(newPlanet));
                })
                .map(Tuple2::getT1);
    }


    @Override
    public Mono<Planet> save(Planet planet) throws BadRequestError {
        List<String> errorMessages = planetValidator.validate(planet);
        if (!errorMessages.isEmpty()) {
            log.warn("Erro ao salvar planeta. name: {}. errors: {}", planet.name(), errorMessages);
            throw new BadRequestError(errorMessages);
        }
        if (planet.id() != null) {
            log.warn("Erro ao salvar planeta, pois contém o ID. name: {}. id: {}", planet.name(), planet.id());
            errorMessages.add("Campo id nao pode ser preenchido.");
            throw new BadRequestError(errorMessages);
        }

        return planetRepository.findByName(planet.name())
                .doOnNext(foundPlanet -> {
                    if (foundPlanet != null) {
                        log.warn("Erro ao salvar planeta. name: {}.", planet.name());
                        throw new ConflictError(format("Planeta com o nome: {0} ja existe.", planet.name()));
                    }
                })
                .switchIfEmpty(planetRepository.save(Mono.just(planet)));
    }

    @Override
    public Mono<Planet> deleteById(String id) {
        return planetRepository.deleteById(id);
    }

    public Flux<Planet> getFromStarWarsClient() {
        return starWarsApiClient.getPlanets()
                .doOnSuccess(planetResponseJson -> {
                    if (planetResponseJson.getResults().isEmpty()) {
                        log.info("Busca de planetas na api do star wars não retornou nenhum resultado.");
                        throw new NotFoundError("Nenhum planeta encontrado.");
                    }
                })
                .map(planetResponseJson -> planetResponseJson.getResults().parallelStream()
                        .map(planetJson -> planetJson.toDomain(null))
                        .map(MongoPlanet::fromDomain)
                        .toList())
                .flatMapMany(mongoPlanets -> planetMongoRepository
                        .saveAll(mongoPlanets)
                        .map(MongoPlanet::toDomain));
    }


    private Mono<Planet> findFromStarWarsClientBy(String name, String id) {
        return starWarsApiClient.getPlanetBy(name)
                .doOnSuccess(planetResponseJson -> {
                    if (planetResponseJson.getResults().isEmpty()) {
                        log.info("Busca de planetas na api do star wars nao retornou nenhum resultado. id: {}. name: {}.", id, name);
                        throw new NotFoundError(format("Nenhum planeta com nome {0} foi encontrado.", name));
                    }
                })
                .map(planetResponseJson -> planetResponseJson.getResults().get(0).toDomain(id));
    }

}


