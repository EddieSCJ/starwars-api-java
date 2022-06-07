package com.api.starwars.planets.domain.model.view;

import com.api.starwars.planets.domain.model.Planet;
import commons.utils.DomainUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PlanetViewTest {

    @Test
    @DisplayName("Deve fazer a conversao para o dominio corretamente.")
    void toDomainSuccessfully() {
        PlanetView planetView = DomainUtils.getRandomPlanetJson();
        Planet expectedPlanet = new Planet(
                planetView.getId(),
                planetView.getName(),
                planetView.getClimate(),
                planetView.getTerrain(),
                planetView.getMovieAppearances(),
                planetView.getCacheInDays()
        );

        assertTrue(Objects.deepEquals(expectedPlanet, planetView.toDomain()));
    }

    @Test
    @DisplayName("Deve fazer a conversao a partir do dominio com sucesso.")
    void fromDomainSuccessfully() {
        Planet planet = DomainUtils.getRandomPlanet();
        PlanetView expectedPlanetView = new PlanetView(
                planet.id(),
                planet.name(),
                planet.climate(),
                planet.terrain(),
                planet.cacheInDays(),
                planet.movieAppearances()
        );

        assertTrue(Objects.deepEquals(expectedPlanetView, PlanetView.fromDomain(planet)));
    }

}
