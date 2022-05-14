package com.api.starwars.planets.model.view;

import com.api.starwars.planets.model.domain.Planet;
import commons.utils.DomainUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlanetJsonTest {

    @Test
    @DisplayName("Deve fazer a conversao para o dominio corretamente.")
    public void toDomainSuccessfully() {
        PlanetJson planetJson = DomainUtils.getRandomPlanetJson();
        Planet expectedPlanet = new Planet(
                planetJson.getId(),
                planetJson.getName(),
                planetJson.getClimate(),
                planetJson.getTerrain(),
                planetJson.getMovieAppearances(),
                planetJson.getCacheInDays()
        );

        assertTrue(Objects.deepEquals(expectedPlanet, planetJson.toDomain()));
    }

    @Test
    @DisplayName("Deve fazer a conversao a partir do dominio com sucesso.")
    public void fromDomainSuccessfully() {
        Planet planet = DomainUtils.getRandomPlanet();
        PlanetJson expectedPlanetJson = new PlanetJson(
                planet.id(),
                planet.name(),
                planet.climate(),
                planet.terrain(),
                planet.cacheInDays(),
                planet.movieAppearances()
        );

        assertTrue(Objects.deepEquals(expectedPlanetJson, PlanetJson.fromDomain(planet)));
    }

}
