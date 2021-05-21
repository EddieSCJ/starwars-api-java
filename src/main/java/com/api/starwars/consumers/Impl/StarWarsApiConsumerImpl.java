package com.api.starwars.consumers.Impl;

import com.api.starwars.consumers.StarWarsApiConsumer;
import com.api.starwars.consumers.dtos.PlanetResponseBodyDTO;
import com.api.starwars.consumers.dtos.PlanetResponseDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class StarWarsApiConsumerImpl implements StarWarsApiConsumer {

    @Override
    public PlanetResponseDTO getPlanetBy(String name) throws IOException, InterruptedException {
        URI apiUri = URI.create(apiAddress + "planets/?search=" + name);
        HttpRequest request = HttpRequest.newBuilder().GET().uri(apiUri).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return buildPlanetResponseDto(response);
    }

    private PlanetResponseDTO buildPlanetResponseDto(HttpResponse<String> response) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        PlanetResponseBodyDTO planetResponseBodyDto = gson.fromJson(response.body(), PlanetResponseBodyDTO.class);
        return PlanetResponseDTO.builder()
                .statusCode(response.statusCode())
                .planetBodyDto(planetResponseBodyDto).build();
    }

}
