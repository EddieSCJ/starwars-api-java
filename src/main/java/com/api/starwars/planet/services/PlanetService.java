package com.api.starwars.planet.services;

import com.api.starwars.planet.mappers.StarWarsApiMapper;
import com.api.starwars.planet.mappers.view.MPlanetJson;
import com.api.starwars.planet.mappers.view.PlanetResponseBodyJson;
import com.api.starwars.planet.mappers.view.PlanetResponseJson;
import com.api.starwars.planet.model.domain.Planet;
import com.api.starwars.planet.model.mongo.MongoPlanet;
import com.api.starwars.planet.model.view.PlanetJson;
import com.api.starwars.planet.repositories.IPlanetMongoRepository;
import com.api.starwars.planet.repositories.IPlanetRepository;
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

@Service
public class PlanetService implements IPlanetService {

    private final StarWarsApiMapper starWarsApiMapper;
    private final IPlanetMongoRepository planetMongoRepository;
    private final IPlanetRepository planetRepository;

    @Autowired
    public PlanetService(StarWarsApiMapper starWarsApiMapper,
                         IPlanetMongoRepository planetMongoRepository,
                         IPlanetRepository planetRepository) {
        this.starWarsApiMapper = starWarsApiMapper;
        this.planetMongoRepository = planetMongoRepository;
        this.planetRepository = planetRepository;
    }

    @Override
    public Page<Planet> findAll(Integer page, String order, String direction, Integer size)
            throws IOException, InterruptedException {

        Sort sort = direction.equals("ASC")
                ? Sort.by(order).ascending()
                : Sort.by(order).descending();

        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Long count = planetRepository.count();

        if (count == 0) {
            List<Planet> planets = findAllFromStarWarsApi();
            return new PageImpl<>(planets, pageRequest, planets.size());
        }

        Page<MongoPlanet> mongoPlanets = planetMongoRepository.findAll(pageRequest);
        return mongoPlanets.map(MongoPlanet::toDomain);
    }

    @Override
    public Planet findById(String id, Long cacheInDays) throws Exception {
        Optional<MongoPlanet> mongoPlanet = planetRepository.findbyId(id);

        if (mongoPlanet.isEmpty()) {
            throw new Exception(); //TODO trocar para notfound exception
        }

        Planet domainPlanet = mongoPlanet.get().toDomain();
        if (domainPlanet.cacheInDays() > cacheInDays) {
            Planet updatedPlanet = findFromStarWarsApiBy(domainPlanet.name(), domainPlanet.id());
            return planetRepository.save(updatedPlanet).toDomain();
        }

        return domainPlanet;
    }

    @Override
    public Planet findByName(String name, Long cacheInDays) throws Exception {
        Optional<MongoPlanet> mongoPlanet = planetRepository.findByName(name);

        if (mongoPlanet.get().toDomain().cacheInDays() > cacheInDays) {
            String id = mongoPlanet.get().toDomain().id();
            Planet planet = findFromStarWarsApiBy(name, id);

            return planetRepository.save(planet).toDomain();
        }

        return mongoPlanet.get().toDomain();
    }

    @Override
    public MongoPlanet save(Planet planet) {
        MongoPlanet mongoPlanet = MongoPlanet.fromDomain(planet);
        return planetRepository.save(mongoPlanet.toDomain());
    }

    @Override
    public List<MongoPlanet> saveAll(List<Planet> planets) {
        List<MongoPlanet> mongoPlanets = planets.parallelStream().map(MongoPlanet::fromDomain).collect(Collectors.toList());
        return planetMongoRepository.saveAll(mongoPlanets);
    }

    @Override
    public void deleteById(String id) {
        planetRepository.deleteById(id);
    }

    public Planet findFromStarWarsApiBy(String name, String id) throws Exception {
        PlanetResponseJson planetResponseJson = starWarsApiMapper.getPlanetBy(name);
        List<MPlanetJson> results = planetResponseJson.planetResponseBodyJson().results();

        if (results.isEmpty()) throw new Exception(); //TODO trocar para notfound

        MPlanetJson mPlanetJson = results.get(0);
        Planet updatedPlanet = new Planet(
                id,
                mPlanetJson.name(),
                mPlanetJson.climate(),
                mPlanetJson.terrain(),
                mPlanetJson.films().size(),
                0L
        );

        return planetRepository.save(updatedPlanet).toDomain();
    }

    @Override
    public List<Planet> findAllFromStarWarsApi() throws IOException, InterruptedException {
        PlanetResponseJson planetResponseJson = starWarsApiMapper.getPlanets();
        PlanetResponseBodyJson planetResponseBodyJson = planetResponseJson.planetResponseBodyJson();
        List<MPlanetJson> results = planetResponseBodyJson.results();

        List<Planet> planets = results
                .parallelStream()
                .map(MPlanetJson::toDomain)
                .collect(Collectors.toList());

        List<MongoPlanet> mongoPlanets = saveAll(planets);
        return mongoPlanets.parallelStream().map(MongoPlanet::toDomain).collect(Collectors.toList());
    }


    @Override
    public List<PlanetJson> planetsToPlanetJson(List<Planet> planets) {
        return planets.parallelStream().map(PlanetJson::fromDomain).collect(Collectors.toList());
    }

}
