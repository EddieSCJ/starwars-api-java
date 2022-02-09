package com.api.starwars.planet.services;

import com.api.starwars.planet.mappers.StarWarsApiMapper;
import com.api.starwars.planet.mappers.view.MPlanetJson;
import com.api.starwars.planet.mappers.view.PlanetResponseBodyJson;
import com.api.starwars.planet.mappers.view.PlanetResponseJson;
import com.api.starwars.planet.model.domain.Planet;
import com.api.starwars.planet.model.mongo.MongoPlanet;
import com.api.starwars.planet.model.view.PlanetJson;
import com.api.starwars.planet.repositories.planets.IPlanetMongoRepository;
import com.api.starwars.planet.repositories.planets.IPlanetRepository;
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
    public PlanetService(StarWarsApiMapper starWarsApiMapper, IPlanetMongoRepository planetMongoRepository,
                         IPlanetRepository planetRepository) {
        this.starWarsApiMapper = starWarsApiMapper;
        this.planetMongoRepository = planetMongoRepository;
        this.planetRepository = planetRepository;
    }

    @Override
    public Page<Planet> findAll(Integer page, String order, String direction, Integer size)
            throws IOException, InterruptedException {

        Sort sort = direction.equals("ASC") ? Sort.by(order).ascending() : Sort.by(order).descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        long count = planetMongoRepository.count();

        if (count == 0) {
            List<Planet> planets = updateWithStarWarsApi();

            return new PageImpl<>(planets, pageRequest, planets.size());
        }

        Page<MongoPlanet> mongoPlanets = planetMongoRepository.findAll(pageRequest);
        List<Planet> planets = mongoPlanets.getContent().parallelStream().map(MongoPlanet::toDomain)
                .collect(Collectors.toList());

        return new PageImpl<>(planets, pageRequest, mongoPlanets.getTotalElements());

    }

    @Override
    public Planet findById(String id, Long cacheInDays) throws Exception {
        Optional<MongoPlanet> mongoPlanet = planetRepository.findbyId(id);

        if (mongoPlanet.isEmpty()) {
            throw new Exception(); //TODO trocar para notfound
        }

        Planet planet = mongoPlanet.get().toDomain();
        if (planet.cacheInDays() > cacheInDays) {
            return searchInStarWarsMapper(planet.name(), planet.id());
        }

        return planet;
    }

    @Override
    public Planet findByName(String name, Long cacheInDays) throws Exception {
        Optional<MongoPlanet> mongoPlanet = planetRepository.findByName(name);

        if (mongoPlanet.isEmpty() || mongoPlanet.get().toDomain().cacheInDays() > cacheInDays) {
            return searchInStarWarsMapper(name, null);
        }

        return mongoPlanet.get().toDomain();
    }

    @Override
    public MongoPlanet save(Planet planet) {
        MongoPlanet mongoPlanet = MongoPlanet.fromDomain(planet);
        return planetMongoRepository.save(mongoPlanet);
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

    public Planet searchInStarWarsMapper(String name, String id) throws Exception {

        PlanetResponseJson planetResponseJson = starWarsApiMapper.getPlanetBy(name);
        PlanetResponseBodyJson planetResponseBodyJson = planetResponseJson.planetResponseBodyJson();
        List<MPlanetJson> results = planetResponseBodyJson.results();
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

        MongoPlanet updatedMongoPlanet = planetRepository.save(updatedPlanet);
        return updatedMongoPlanet.toDomain();
    }

    @Override
    public List<Planet> updateWithStarWarsApi() throws IOException, InterruptedException {
        PlanetResponseJson planetResponseJson = starWarsApiMapper.getPlanets();
        PlanetResponseBodyJson planetResponseBodyJson = planetResponseJson.planetResponseBodyJson();
        List<MPlanetJson> results = planetResponseBodyJson.results();

        List<Planet> planets = results
                .parallelStream()
                .map(planetJson ->
                        new Planet(
                                null,
                                planetJson.name(),
                                planetJson.climate(),
                                planetJson.terrain(),
                                planetJson.films().size(),
                                0L
                        )
                ).collect(Collectors.toList());

        List<MongoPlanet> mongoPlanets = saveAll(planets);
        return mongoPlanets.parallelStream().map(MongoPlanet::toDomain).collect(Collectors.toList());

    }


    @Override
    public List<PlanetJson> planetsToPlanetJson(List<Planet> planets) {

        return planets.parallelStream().map(PlanetJson::fromDomain).collect(Collectors.toList());
    }

}
