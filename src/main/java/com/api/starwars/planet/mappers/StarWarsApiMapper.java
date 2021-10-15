package com.api.starwars.planet.mappers;

import com.api.starwars.mappers.StarWarsApiConsumer;
import com.api.starwars.planet.mappers.view.PlanetResponseBodyJson;
import com.api.starwars.planet.mappers.view.PlanetResponseJson;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class StarWarsApiMapper implements IStarWarsApiMapper {

    @Override
    public PlanetResponseJson getPlanetBy(String name) throws IOException, InterruptedException {
        URI apiUri = URI.create(apiAddress + "planets/?search=" + name);
        HttpRequest request = HttpRequest.newBuilder().GET().uri(apiUri).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return buildPlanetResponseDto(response);
    }

    private PlanetResponseJson buildPlanetResponseDto(HttpResponse<String> response) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        PlanetResponseBodyJson planetResponseBodyJson = gson.fromJson(response.body(), PlanetResponseBodyJson.class);

        return PlanetResponseJson.builder()
                .statusCode(response.statusCode())
                .planetBodyDto(planetResponseBodyJson).build();
    }

}
