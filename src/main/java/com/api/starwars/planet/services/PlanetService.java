package com.api.starwars.planet.services;

import com.api.starwars.consumers.StarWarsApiConsumer;
import com.api.starwars.consumers.dtos.PlanetResponseDTO;
import com.api.starwars.domain.Planet;
import com.api.starwars.domain.dtos.PlanetDTO;
import com.api.starwars.planet.mappers.StarWarsApiMapper;
import com.api.starwars.planet.mappers.view.PlanetResponseJson;
import com.api.starwars.planet.model.domain.Planet;
import com.api.starwars.planet.model.mongo.MongoPlanet;
import com.api.starwars.planet.model.view.PlanetJson;
import com.api.starwars.planet.repositories.planets.IPlanetRepository;
import com.api.starwars.repositories.PlanetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlanetService implements IPlanetService {

    @Autowired
    StarWarsApiMapper starWarsApiMapper;

    @Autowired
    IPlanetRepository planetRepository;

    @Value("${com.api.starwars.page_size.default}")
    private Integer pageSizeDefault;

    /**
     * Como a API usa um tipo de LIKE pra fazer a busca de planetas
     * vou assumir que a posição 0 é a mais próxima do que foi pedido
     */

    @Override
    public Integer getMoviesAppearancesQuantity(String planetName)  {
        PlanetResponseJson planets = starWarsApiMapper.getPlanetBy(planetName);
        if (planets.getPlanetResponseBodyJson().getCount() == 0) return 0;
        return planets.getPlanetResponseBodyJson().getResults().get(0).getFilms().size();
    }

    @Override
    public List<Planet> findAll()  {
        List<MongoPlanet> mongoPlanets = planetRepository.findAll();
        return mongoPlanets.stream().map(MongoPlanet::toDomain).collect(Collectors.toList());

    }

    @Override
    public Page<PlanetJson> findAll(Integer page, String order, String direction)  {
        Sort sort = direction.equals("ASC") ? Sort.by(order).ascending() : Sort.by(order).descending();
        PageRequest pageRequest = PageRequest.of(page, pageSizeDefault, sort);

        Page<MongoPlanet> mongoPlanets = planetRepository.findAll(pageRequest);

        List<PlanetJson> planets = new ArrayList<>();

        for (MongoPlanet mongoPlanet : mongoPlanets) {
            Planet planet = mongoPlanet.toDomain();
            planets.add(new PlanetJson(planet, getMoviesAppearancesQuantity(planet.getName())));
        }

        return new PageImpl<>(planets);

    }

    @Override
    public Optional<Planet> findById(String id)  {
        MongoPlanet mongoPlanet = planetRepository.findbyId(id);
        return optionalPlanet(mongoPlanet);
    }

    @Override
    public Optional<Planet> findByName(String name)  {
        MongoPlanet mongoPlanet = planetRepository.findByName(name);
        return optionalPlanet(mongoPlanet);
    }

    @Override
    public Planet save(Planet planet) {
        MongoPlanet mongoPlanet = new MongoPlanet(planet);
        return planetRepository.save(mongoPlanet).toDomain();
    }

    @Override
    public void deleteById(String id) {
        planetRepository.deleteById(id);
    }

    // TODO transfor it in an optional
    @Override
    public Map<String, Object> alreadyExists(String name, String id) {
        Planet planet = planetRepository.findByNameOrId(name, id);
        Map<String, Object> map = new HashMap<>();

        map.put("alreadyExists", planet != null);
        map.put("planet", planet);

        return map;
    }


    public Optional<Planet> optionalPlanet(MongoPlanet mongoPlanet) {
        if(mongoPlanet == null) return Optional.empty();
        else return Optional.of(mongoPlanet.toDomain());
    }
}
