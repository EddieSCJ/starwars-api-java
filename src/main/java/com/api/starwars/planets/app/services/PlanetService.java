package com.api.starwars.planets.app.services;

import com.api.starwars.common.exceptions.http.HttpBadGatewayException;
import com.api.starwars.common.exceptions.http.HttpBadRequestException;
import com.api.starwars.common.exceptions.http.HttpNotFoundException;
import com.api.starwars.planets.app.storage.mongo.IPlanetMongoRepository;
import com.api.starwars.planets.app.storage.mongo.model.MongoPlanet;
import com.api.starwars.planets.app.validations.PlanetValidator;
import com.api.starwars.planets.domain.client.StarWarsApiClient;
import com.api.starwars.planets.domain.model.Planet;
import com.api.starwars.planets.domain.model.client.PlanetJson;
import com.api.starwars.planets.domain.model.client.PlanetResponseJson;
import com.api.starwars.planets.domain.operations.PlanetOperations;
import com.api.starwars.planets.domain.storage.PlanetStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public Page<Planet> findAll(Integer page, String order, String direction, Integer size) {

        Sort sort = direction.equals("ASC")
                ? Sort.by(order).ascending()
                : Sort.by(order).descending();

        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Long count = planetRepository.count();

        if (count == 0) {
            log.info("Busca de planetas no banco nao retornou nenhum resultado.");
            List<Planet> planets = findAllFromStarWarsApi();
            List<Planet> savedPlanets = saveAll(planets);

            return new PageImpl<>(savedPlanets, pageRequest, savedPlanets.size());
        }

        Page<MongoPlanet> mongoPlanets = planetMongoRepository.findAll(pageRequest);
        return mongoPlanets.map(MongoPlanet::toDomain);
    }

    @Override
    public Planet findById(String id, Long cacheInDays) {
        Optional<Planet> storedPlanet = planetRepository.findById(id);
        if (storedPlanet.isEmpty()) {
            throwNotFound(id);
        }

        Planet domainPlanet = storedPlanet.get();
        if (domainPlanet.cacheInDays() > cacheInDays) {
            log.info("Busca de planetas no banco por id retornou um resultado com cache expirado. id: {}. cacheInDays: {}.", id, cacheInDays);

            Planet updatedPlanet = findFromStarWarsApiBy(domainPlanet.name(), domainPlanet.id());
            return planetRepository.save(updatedPlanet);
        }

        return domainPlanet;
    }

    @Override
    public Planet findByName(String name, Long cacheInDays) {
        Optional<Planet> storedPlanet = planetRepository.findByName(name);

        if (storedPlanet.isEmpty()) {
            log.info("Busca de planetas por nome nao retornou nenhum resultado. name: {}.", name);
            Planet planet = findFromStarWarsApiBy(name, null);
            return planetRepository.save(planet);
        }

        Planet domainPlanet = storedPlanet.get();
        if (domainPlanet.cacheInDays() > cacheInDays) {
            log.info("Busca de planetas no banco por id retornou um resultado com cache expirado. name: {}. cacheInDays: {}.", name, cacheInDays);
            Planet planet = findFromStarWarsApiBy(name, domainPlanet.id());

            return planetRepository.save(planet);
        }

        return storedPlanet.get();
    }

    @Override
    public Planet updateById(String id, Planet planet) {
        List<String> errorMessages = planetValidator.validate(planet);
        if (id == null) {
            log.warn("Erro ao atualizar planeta. id {}. name: {}.", planet.id(), planet.name());
            errorMessages.add("Campo id nao pode ser nulo.");
            throw new HttpBadRequestException(errorMessages);
        }

        if (!errorMessages.isEmpty()) {
            log.warn("Erro ao atualizar planeta. id {}. name: {}.", planet.id(), planet.name());
            throw new HttpBadRequestException(errorMessages);
        }

        Optional<Planet> storedPlanet = planetRepository.findById(id);
        if (storedPlanet.isEmpty()) throwNotFound(id);

        Planet newPlanet = new Planet(
                id,
                planet.name(),
                planet.climate(),
                planet.terrain(),
                planet.movieAppearances(),
                null
        );
        return planetRepository.save(newPlanet);
    }

    @Override
    public Planet save(Planet planet) throws HttpBadRequestException {
        List<String> errorMessages = planetValidator.validate(planet);
        if (!errorMessages.isEmpty()) {
            log.warn("Erro ao salvar planeta. name: {}.", planet.name());
            throw new HttpBadRequestException(errorMessages);
        }

        return planetRepository.save(planet);
    }

    @Override
    public List<Planet> saveAll(List<Planet> planets) {
        List<MongoPlanet> mongoPlanets = planets.parallelStream().map(MongoPlanet::fromDomain).toList();
        return planetMongoRepository.saveAll(mongoPlanets).stream().map(MongoPlanet::toDomain).toList();
    }

    @Override
    public void deleteById(String id) {
        planetRepository.deleteById(id);
    }

    private Planet findFromStarWarsApiBy(String name, String id) {
        PlanetResponseJson planetResponseJson;
        try {
            planetResponseJson = starWarsApiClient.getPlanetBy(name);
        } catch (IOException | InterruptedException e) {
            throw new HttpBadGatewayException(new ArrayList<>(List.of("swapi server thrown an exception")));
        }

        List<PlanetJson> results = planetResponseJson.getResults();

        if (results.isEmpty()) {
            log.info("Busca de planetas na api do star wars nao retornou nenhum resultado. id: {}. name: {}.", id, name);
            throw new HttpNotFoundException(format("Nenhum planeta com nome {0} foi encontrado.", name));
        }

        PlanetJson planetJson = results.get(0);
        return planetJson.toDomain(id);

    }

    private List<Planet> findAllFromStarWarsApi() {
        PlanetResponseJson planetResponseJson = starWarsApiClient.getPlanets();
        List<PlanetJson> results = planetResponseJson.getResults();

        if (results.isEmpty()) {
            log.info("Busca de planetas na api do star wars não retornou nenhum resultado.");
            throw new HttpNotFoundException("Nenhum planeta encontrado.");
        }

        return results
                .parallelStream()
                .map(planet -> planet.toDomain(null))
                .toList();
    }

    private void throwNotFound(String id) {
        log.info("Busca de planetas por id nao retornou nenhum resultado. id: {}.", id);
        throw new HttpNotFoundException(format("Planeta com id {0} não encontrado.", id));
    }

}


