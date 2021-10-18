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
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlanetService implements IPlanetService {

    @Autowired
    StarWarsApiMapper starWarsApiMapper;

    @Autowired
    IPlanetMongoRepository planetMongoRepository;

    @Autowired
    IPlanetRepository planetRepository;

    @Override
    public Page<Planet> findAll(Integer page, String order, String direction, Integer size)
            throws IOException, InterruptedException {

        Sort sort = direction.equals("ASC") ? Sort.by(order).ascending() : Sort.by(order).descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        long count = planetMongoRepository.count();

        if (count == 0) {
            PlanetResponseJson planetResponseJson = starWarsApiMapper.getPlanets();
            PlanetResponseBodyJson planetResponseBodyJson = planetResponseJson.getPlanetResponseBodyJson();
            List<MPlanetJson> results = planetResponseBodyJson.getResults();

            List<Planet> planets = results
                    .parallelStream()
                    .map(planetJson ->
                            Planet.builder()
                                    .id(null)
                                    .name(planetJson.getName())
                                    .climate(planetJson.getClimate())
                                    .terrain(planetJson.getTerrain())
                                    .movieAppeareces(planetJson.getFilms().size())
                                    .build()
                    ).collect(Collectors.toList());

            List<MongoPlanet> mongoPlanets = saveAll(planets);
            planets = mongoPlanets.parallelStream().map(MongoPlanet::toDomain).collect(Collectors.toList());

            return new PageImpl<>(planets, pageRequest, mongoPlanets.size());
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
        if (planet.getCacheInDays() > cacheInDays) {
          return searchInStarWarsMapper(planet.getName(), planet.getId());
        }

        return planet;
    }

    @Override
    public Planet findByName(String name, Long cacheInDays) throws Exception {
        Optional<MongoPlanet> mongoPlanet = planetRepository.findByName(name);

        if (mongoPlanet.isEmpty() || mongoPlanet.get().toDomain().getCacheInDays() > cacheInDays) {
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

    private Planet searchInStarWarsMapper(String name, String id) throws Exception {

        PlanetResponseJson planetResponseJson = starWarsApiMapper.getPlanetBy(name);
        PlanetResponseBodyJson planetResponseBodyJson = planetResponseJson.getPlanetResponseBodyJson();
        List<MPlanetJson> results = planetResponseBodyJson.getResults();
        if(results.isEmpty()) throw new Exception(); //TODO trocar para notfound

        MPlanetJson mPlanetJson = results.get(0);
        Planet updatedPlanet = Planet.builder()
                .id(id)
                .name(mPlanetJson.getName())
                .climate(mPlanetJson.getClimate())
                .terrain(mPlanetJson.getTerrain())
                .movieAppeareces(mPlanetJson.getFilms().size())
                .build();

        MongoPlanet updatedMongoPlanet = planetRepository.save(updatedPlanet);
        return updatedMongoPlanet.toDomain();
    }

    @Override
    public List<PlanetJson> planetsToPlanetJson(List<Planet> planets) {

        return planets.parallelStream().map(PlanetJson::fromDomain).collect(Collectors.toList());
    }

}
