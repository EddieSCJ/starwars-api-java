package com.api.starwars.domain.planets.services;

import com.api.starwars.commons.exceptions.http.HttpNotFoundException;
import com.api.starwars.domain.planets.clients.IStarWarsApiClient;
import com.api.starwars.domain.planets.clients.view.PlanetResponseJson;
import com.api.starwars.domain.planets.model.domain.Planet;
import com.api.starwars.domain.planets.model.mongo.MongoPlanet;
import com.api.starwars.domain.planets.repositories.IPlanetMongoRepository;
import com.api.starwars.domain.planets.repositories.IPlanetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import utils.Domain;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static utils.Domain.FAKE_ID;

public class PlanetServiceTest {

    @Mock
    IPlanetMongoRepository planetMongoRepository;

    @Mock
    IPlanetRepository planetRepository;

    @Mock
    IStarWarsApiClient starWarsApiClient;

    @InjectMocks
    PlanetService planetService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void find_all_successfully_from_database() {
        Page<MongoPlanet> originalPage = new PageImpl<>(Domain.getRandomMongoPlanetList());
        when(planetRepository.count()).thenReturn(3L);
        when(planetMongoRepository.findAll(any(PageRequest.class))).thenReturn(originalPage);

        Page<Planet> returnedPage = planetService.findAll(1, "name", Sort.Direction.ASC.name(), 15);
        assertEquals(originalPage.getTotalElements(), returnedPage.getTotalElements());
        assertEquals(originalPage.getTotalPages(), returnedPage.getTotalPages());
        assertEquals(originalPage.getSort(), returnedPage.getSort());
        assertEquals(originalPage.getNumber(), returnedPage.getNumber());
        assertEquals(originalPage.getNumberOfElements(), returnedPage.getNumberOfElements());
    }

    @Test
    public void find_all_successfully_from_star_wars_api_client() {
        PlanetResponseJson planetResponseJson = Domain.getPlanetResponseJson();
        List<MongoPlanet> mongoPlanets = planetResponseJson.getResults().stream()
                .map(mPlanetJson -> mPlanetJson.toDomain(null))
                .map(MongoPlanet::fromDomain)
                .toList();

        when(planetRepository.count()).thenReturn(0L);
        when(starWarsApiClient.getPlanets()).thenReturn(planetResponseJson);
        when(planetMongoRepository.saveAll(anyList())).thenReturn(mongoPlanets);

        Page<Planet> returnedPage = planetService.findAll(0, "name", Sort.Direction.ASC.name(), 15);

        assertEquals(1, returnedPage.getTotalPages());
        assertEquals(0, returnedPage.getNumber());
        assertEquals(1, returnedPage.getNumberOfElements());
        assertEquals(1, returnedPage.getTotalElements());
        assertEquals(Sort.by("name").ascending(), returnedPage.getSort());
    }

    @Test
    public void find_all_fail_when_not_found_from_star_wars_api_client() {
        PlanetResponseJson planetResponseJson = Domain.getEmptyPlanetResponseJson();

        when(planetRepository.count()).thenReturn(0L);
        when(starWarsApiClient.getPlanets()).thenReturn(planetResponseJson);

        assertThrows(HttpNotFoundException.class, () -> planetService.findAll(0, "name", Sort.Direction.ASC.name(), 15));
    }

    @Test
    public void find_by_id_successfully() throws IOException, InterruptedException {
        MongoPlanet mongoPlanet = Domain.getRandomMongoPlanet();
        when(planetRepository.findById(FAKE_ID)).thenReturn(Optional.of(mongoPlanet));

        Planet planet = planetService.findById(FAKE_ID, 0L);

        assertTrue(Objects.deepEquals(mongoPlanet.toDomain(), planet));
    }

    @Test
    public void find_by_id_fail_when_not_found() {
        when(planetRepository.findById(FAKE_ID)).thenReturn(Optional.empty());
        assertThrows(HttpNotFoundException.class, () -> planetService.findById(FAKE_ID, 0L));
    }

    @Test
    public void find_by_id_successfully_from_star_wars_api_client() throws IOException, InterruptedException {
        MongoPlanet mongoPlanet = Domain.getRandomMongoPlanet();
        mongoPlanet.setCreationDate(LocalDateTime.of(2001, 12, 12, 12, 12));

        PlanetResponseJson planetResponseJson = Domain.getPlanetResponseJson();
        planetResponseJson.getResults().get(0).setName(mongoPlanet.getName());

        Planet apiClientPlanet = planetResponseJson.getResults().get(0).toDomain(mongoPlanet.getId());
        MongoPlanet savedPlanet = MongoPlanet.fromDomain(apiClientPlanet);

        when(planetRepository.findById(FAKE_ID)).thenReturn(Optional.of(mongoPlanet));
        when(planetRepository.save(any(Planet.class))).thenReturn(savedPlanet);
        when(starWarsApiClient.getPlanetBy(mongoPlanet.getName())).thenReturn(planetResponseJson);

        Planet planet = planetService.findById(FAKE_ID, 0L);
        assertTrue(Objects.deepEquals(savedPlanet.toDomain(), planet));
    }

    @Test
    public void find_by_id_fail_when_not_found_from_star_wars_api() throws IOException, InterruptedException {
        MongoPlanet mongoPlanet = Domain.getRandomMongoPlanet();
        mongoPlanet.setCreationDate(LocalDateTime.of(2001, 12, 12, 12, 12));

        PlanetResponseJson planetResponseJson = Domain.getEmptyPlanetResponseJson();

        when(planetRepository.findById(FAKE_ID)).thenReturn(Optional.of(mongoPlanet));
        when(starWarsApiClient.getPlanetBy(mongoPlanet.getName())).thenReturn(planetResponseJson);

        assertThrows(HttpNotFoundException.class, () -> planetService.findById(FAKE_ID, 0L));
    }

}

