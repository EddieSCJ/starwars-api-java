package com.api.starwars.planet.mappers;

import com.api.starwars.planet.mappers.view.PlanetResponseBodyJson;
import com.api.starwars.planet.mappers.view.PlanetResponseJson;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@Service
public class StarWarsApiMapper implements IStarWarsApiMapper {

    @Override
    public PlanetResponseJson getPlanetBy(String name) throws IOException, InterruptedException {
        log.info("Iniciando busca de planeta pelo nome na api do star wars. name: {}", name);
        URI apiUri = URI.create(apiAddress + "planets/?search=" + name);
        PlanetResponseJson planet = buildPlanetResponseJson(makeRequest(apiUri));

        log.info("Finalizando busca de planeta pelo nome na api do star wars. name: {}", name);
        return planet;
    }

    public PlanetResponseJson getPlanets() throws IOException, InterruptedException {
        log.info("Iniciando busca de planetas na api do star wars.");
        URI apiUri = URI.create(apiAddress + "planets");
        PlanetResponseJson planet = buildPlanetResponseJson(makeRequest(apiUri));

        log.info("Finalizando busca de planetas na api do star wars.");
        return planet;
    }

    private HttpResponse<String> makeRequest(URI apiUri) throws IOException, InterruptedException {
        log.info("Iniciando envio de requisição para a api do star wars. uri: {}", apiUri);
        HttpRequest request = HttpRequest.newBuilder().GET().uri(apiUri).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        log.info("Finalizando envio de requisição para a api do star wars. uri: {}", apiUri);
        return response;
    }

    private PlanetResponseJson buildPlanetResponseJson(HttpResponse<String> response) {
        log.info("Iniciando construção do objeto PlanetResponseJson a partir da resposta da api do star wars.");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        PlanetResponseBodyJson planetResponseBodyJson = gson.fromJson(response.body(), PlanetResponseBodyJson.class);
        PlanetResponseJson planet = new PlanetResponseJson(response.statusCode(), planetResponseBodyJson);

        log.info("Finalizando construção do objeto PlanetResponseJson a partir da resposta da api do star wars.");
        return planet;
    }

}
