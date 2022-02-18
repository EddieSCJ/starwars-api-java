package com.api.starwars.domain.planets.mappers;

import com.api.starwars.domain.planets.mappers.view.PlanetResponseJson;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.time.Duration;

public interface IStarWarsApiMapper {

    HttpClient httpClient = HttpClient.newBuilder()
            .version(Version.HTTP_2)
            .followRedirects(Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    @Deprecated
    String api = "https://swapi.co/";

    String apiAddress = "https://swapi.dev/api/";

    PlanetResponseJson getPlanetBy(String name) throws IOException, InterruptedException;

}
