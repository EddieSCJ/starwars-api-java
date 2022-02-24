package com.api.starwars.domain.planets.services;

import com.api.starwars.commons.exceptions.http.HttpBadRequestException;
import com.api.starwars.commons.exceptions.http.HttpNotFoundException;
import com.api.starwars.domain.planets.clients.IStarWarsApiClient;
import com.api.starwars.domain.planets.clients.view.MPlanetJson;
import com.api.starwars.domain.planets.clients.view.PlanetResponseJson;
import com.api.starwars.domain.planets.model.domain.Planet;
import com.api.starwars.domain.planets.model.mongo.MongoPlanet;
import com.api.starwars.domain.planets.repositories.IPlanetMongoRepository;
import com.api.starwars.domain.planets.repositories.IPlanetRepository;
import com.api.starwars.domain.planets.validations.PlanetValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.text.MessageFormat.format;

@Slf4j
@Service
public class PlanetService implements IPlanetService {

    private final IStarWarsApiClient starWarsApiClient;
    private final IPlanetMongoRepository planetMongoRepository;
    private final IPlanetRepository planetRepository;

    @Autowired
    public PlanetService(IStarWarsApiClient starWarsApiMapper,
                         IPlanetMongoRepository planetMongoRepository,
                         IPlanetRepository planetRepository) {
        this.starWarsApiClient = starWarsApiMapper;
        this.planetMongoRepository = planetMongoRepository;
        this.planetRepository = planetRepository;
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
    public Planet findById(String id, Long cacheInDays) throws IOException, InterruptedException {
        Optional<MongoPlanet> mongoPlanet = planetRepository.findbyId(id);
        if (mongoPlanet.isEmpty()) {
            log.info("Busca de planetas por id nao retornou nenhum resultado. id: {}.", id);
            throw new HttpNotFoundException(format("Planeta com id {0} não encontrado.", id));
        }


        Planet domainPlanet = mongoPlanet.get().toDomain();
        if (domainPlanet.cacheInDays() > cacheInDays) {
            log.info("Busca de planetas no banco por id retornou um resultado com cache expirado. id: {}. cacheInDays: {}.", id, cacheInDays);

            Planet updatedPlanet = findFromStarWarsApiBy(domainPlanet.name(), domainPlanet.id());

            return planetRepository.save(updatedPlanet).toDomain();
        }

        return domainPlanet;
    }

    @Override
    public Planet findByName(String name, Long cacheInDays) throws IOException, InterruptedException {
        Optional<MongoPlanet> mongoPlanet = planetRepository.findByName(name);

        if (mongoPlanet.isEmpty()) {
            log.info("Busca de planetas por nome nao retornou nenhum resultado. name: {}.", name);
            Planet planet = findFromStarWarsApiBy(name, null);
            return planetRepository.save(planet).toDomain();
        }

        Planet domainPlanet = mongoPlanet.get().toDomain();
        if (domainPlanet.cacheInDays() > cacheInDays) {
            log.info("Busca de planetas no banco por id retornou um resultado com cache expirado. name: {}. cacheInDays: {}.", name, cacheInDays);
            Planet planet = findFromStarWarsApiBy(name, domainPlanet.id());

            return planetRepository.save(planet).toDomain();
        }

        return mongoPlanet.get().toDomain();
    }

    @Override
    public Planet save(Planet planet) throws HttpBadRequestException {
        final PlanetValidator planetValidator = new PlanetValidator();
        List<String> errorMessages = planetValidator.validate(planet);
        if (!errorMessages.isEmpty()) {
            log.warn("Erro ao salvar planeta. name: {}.", planet.name());
            throw new HttpBadRequestException(errorMessages);
        }

        MongoPlanet mongoPlanet = MongoPlanet.fromDomain(planet);
        return planetRepository.save(mongoPlanet.toDomain()).toDomain();
    }

    @Override
    public List<Planet> saveAll(List<Planet> planets) {
        List<MongoPlanet> mongoPlanets = planets.parallelStream().map(MongoPlanet::fromDomain).collect(Collectors.toList());
        return planetMongoRepository.saveAll(mongoPlanets)
                .parallelStream()
                .map(MongoPlanet::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        planetRepository.deleteById(id);
    }

    public Planet findFromStarWarsApiBy(String name, String id) throws IOException, InterruptedException {
        PlanetResponseJson planetResponseJson = starWarsApiClient.getPlanetBy(name);
        List<MPlanetJson> results = planetResponseJson.getResults();

        if (results.isEmpty()) {
            log.info("Busca de planetas na api do star wars nao retornou nenhum resultado. id: {}. name: {}.", id, name);
            throw new HttpNotFoundException(format("Planeta com nome {0} não encontrado.", name));
        }

        MPlanetJson mPlanetJson = results.get(0);
        return mPlanetJson.toDomain(id);

    }

    @Override
    public List<Planet> findAllFromStarWarsApi() {
        PlanetResponseJson planetResponseJson = starWarsApiClient.getPlanets();
        List<MPlanetJson> results = planetResponseJson.getResults();

        if (results.isEmpty()) {
            log.info("Busca de planetas na api do star wars não retornou nenhum resultado.");
            throw new HttpNotFoundException("Nenhum planeta encontrado.");
        }

        return results
                .parallelStream()
                .map(planet -> planet.toDomain(null))
                .collect(Collectors.toList());
    }


}
