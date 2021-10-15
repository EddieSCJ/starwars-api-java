package com.api.starwars.planet.repositories.planets;


import com.api.starwars.planet.model.mongo.MongoPlanet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IPlanetRepository {

    List<MongoPlanet> findAll();

    Page<MongoPlanet> findAll(PageRequest page);

    MongoPlanet findByName(String name);

    MongoPlanet findbyId(String id);

    MongoPlanet findByNameOrId(String name, String id);

    MongoPlanet save(MongoPlanet mongoPlanet);

    void deleteById(String id);
}
