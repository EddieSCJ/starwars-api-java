package com.api.starwars.domain.planets.model.mongo;

import com.api.starwars.domain.planets.model.domain.Planet;
import commons.utils.DomainUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MongoPlanetTest {

    @Test
    @DisplayName("Deve fazer a conversao para o dominio corretamente.")
    public void toDomainSuccessfully() {
        MongoPlanet mongoPlanet = DomainUtils.getRandomMongoPlanet();
        Planet expectedDomain = new Planet(
                mongoPlanet.getId(),
                mongoPlanet.getName(),
                mongoPlanet.getClimate(),
                mongoPlanet.getTerrain(),
                mongoPlanet.getMovieAppearances(),
                Duration.between(LocalDateTime.now(), mongoPlanet.getCreationDate()).toDays()
        );

        assertTrue(Objects.deepEquals(expectedDomain, mongoPlanet.toDomain()));
    }

    @Test
    @DisplayName("Deve fazer a conversao a partir do dominio corretamente.")
    public void fromDomainSuccessfully() {
        LocalDateTime dateTime = LocalDateTime.now();

        try (MockedStatic<LocalDateTime> mock = Mockito.mockStatic(LocalDateTime.class)) {
            mock.when(LocalDateTime::now).thenReturn(dateTime);

            Planet planet = DomainUtils.getRandomPlanet();
            MongoPlanet expectedMongoPlanet = new MongoPlanet(
                    planet.id(),
                    planet.name(),
                    planet.climate(),
                    planet.terrain(),
                    planet.movieAppearances(),
                    LocalDateTime.now()
                    );

            assertTrue(Objects.deepEquals(expectedMongoPlanet, MongoPlanet.fromDomain(planet)));
        }
    }

}
