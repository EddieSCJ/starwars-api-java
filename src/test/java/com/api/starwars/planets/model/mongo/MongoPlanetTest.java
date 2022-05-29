package com.api.starwars.planets.model.mongo;

import com.api.starwars.planets.model.domain.Planet;
import commons.utils.DomainUtils;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MongoPlanetTest {

    @Test
    @DisplayName("Deve fazer a conversao para o dominio corretamente.")
    void toDomainSuccessfully() {
        MongoPlanet mongoPlanet = DomainUtils.getRandomMongoPlanet();
        Planet expectedDomain = new Planet(
                mongoPlanet.getId().toString(),
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
    void fromDomainSuccessfully() {
        LocalDateTime dateTime = LocalDateTime.now();

        try (MockedStatic<LocalDateTime> mock = Mockito.mockStatic(LocalDateTime.class)) {
            mock.when(LocalDateTime::now).thenReturn(dateTime);

            Planet planet = DomainUtils.getRandomPlanet();
            MongoPlanet expectedMongoPlanet = new MongoPlanet(
                    new ObjectId(planet.id()),
                    planet.name(),
                    planet.climate(),
                    planet.terrain(),
                    planet.movieAppearances(),
                    LocalDateTime.now()
                    );

            MongoPlanet mongoPlanet = MongoPlanet.fromDomain(planet);
            assertEquals(expectedMongoPlanet.getId(), mongoPlanet.getId());
            assertEquals(expectedMongoPlanet.getCreationDate(), mongoPlanet.getCreationDate());
            assertEquals(expectedMongoPlanet.getName(), mongoPlanet.getName());
            assertEquals(expectedMongoPlanet.getMovieAppearances(), mongoPlanet.getMovieAppearances());
            assertEquals(expectedMongoPlanet.getClimate().length, mongoPlanet.getClimate().length);
            assertEquals(expectedMongoPlanet.getTerrain().length, mongoPlanet.getTerrain().length);
        }
    }

}
