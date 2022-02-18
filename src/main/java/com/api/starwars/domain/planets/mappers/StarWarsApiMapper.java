package com.api.starwars.domain.planets.mappers;

import com.api.starwars.domain.planets.mappers.view.PlanetResponseJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Slf4j
@Service
public class StarWarsApiMapper implements IStarWarsApiMapper {

    private final RestTemplate starwarsApiRestTemplate;

    @Autowired
    public StarWarsApiMapper(RestTemplate starwarsApiRestTemplate) {
        this.starwarsApiRestTemplate = starwarsApiRestTemplate;
    }

    @Override
    public PlanetResponseJson getPlanetBy(String name) {
        log.info("Iniciando busca de planeta pelo nome na api do star wars. name: {}", name);
        URI apiUri = URI.create(apiAddress + "planets/?search=" + name);
        PlanetResponseJson planet = makeRequest(apiUri);

        log.info("Busca de planeta pelo nome na api do star wars concluida com sucesso. name: {}", name);
        return planet;
    }

    public PlanetResponseJson getPlanets() {
        log.info("Iniciando busca de planetas na api do star wars.");
        URI apiUri = URI.create(apiAddress + "planets");
        PlanetResponseJson planet = makeRequest(apiUri);

        log.info("Busca de planetas na api do star wars concluida com sucesso.");
        return planet;
    }

    private PlanetResponseJson makeRequest(URI apiUri) {
        log.info("Iniciando envio de requisicao para a api do star wars. uri: {}", apiUri);
        PlanetResponseJson response = starwarsApiRestTemplate.getForObject(apiUri.toString(), PlanetResponseJson.class);

        log.info("Envio de requisicao para a api do star wars concluido com sucesso. uri: {}", apiUri);
        return response;
    }

}
