package com.api.starwars.domain.planets.mappers;

import com.api.starwars.domain.planets.mappers.view.PlanetResponseJson;

import java.io.IOException;

public interface IStarWarsApiMapper {

    @Deprecated
    String api = "https://swapi.co/";

    String apiAddress = "https://swapi.dev/api/";

    PlanetResponseJson getPlanetBy(String name) throws IOException, InterruptedException;

}
