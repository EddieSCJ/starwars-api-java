package com.api.starwars.consumers;

import com.api.starwars.consumers.dtos.PlanetResponseDTO;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpClient.Redirect;
import java.time.Duration;

public interface StarWarsApiConsumer {

    HttpClient httpClient = HttpClient.newBuilder()
            .version(Version.HTTP_2)
            .followRedirects(Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(20))
            .build();

    @Deprecated
    String api = "https://swapi.co/";

    String apiAddress = "https://swapi.dev/api/";

    PlanetResponseDTO getPlanetBy(String name) throws IOException, InterruptedException;

}
