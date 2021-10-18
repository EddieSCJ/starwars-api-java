package com.api.starwars.services;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PlanetHandlerJsonServiceTest {

    private final static com.api.starwars.consumers.dtos.PlanetDTO rightPlanetDTO = com.api.starwars.consumers.dtos.PlanetDTO.builder().films(Arrays.asList("film1", "film2", "film3", "film4", "film5")).build();
    private final static PlanetResponseBodyDTO rightPlanetReponseBodyDTO = PlanetResponseBodyDTO.builder().results(Collections.singletonList(rightPlanetDTO)).count(1).build();
    private final static PlanetResponseDTO rightPlanetResponseDTO = PlanetResponseDTO.builder().statusCode(200).planetBodyDto(rightPlanetReponseBodyDTO).build();

    private final static PlanetResponseBodyDTO wrongPlanetReponseBodyDTO = PlanetResponseBodyDTO.builder().results(Collections.emptyList()).count(0).build();
    private final static PlanetResponseDTO wrongPlanetResponseDTO = PlanetResponseDTO.builder().statusCode(200).planetBodyDto(wrongPlanetReponseBodyDTO).build();

    private final static String RIGHT_PLANET_NAME = "Tatooine";
    private final static String WRONG_PLANET_NAME = "Tatooine Do Meio Fio";
    private final static Integer EXPTECTED_QUANTITY_OF_APPEARANCES = 5;
    private final static Integer EXPTECTED_QUANTITY_OF_APPEARANCES_WHEN_WRONG_NAME = 0;
    private final static Planet PLANET = new Planet(RIGHT_PLANET_NAME, "Semiarido", "Arenoso");
    private final static Planet WRONG_PLANET = new Planet(WRONG_PLANET_NAME, "Arido", "Molhado");

    private final static Sort SORT = Sort.by("name").ascending();
    private final static Integer RIGHT_PAGE = 0;
    private final static Integer WRONG_PAGE = 12;
    private final static Integer LENGHT = 15;

    private final static String RIGHT_ID = "hd2gbu34b";
    private final static String WRONG_ID = "hd2gbu3SDFKNSADOFM4b";

    @Autowired
    private com.api.starwars.services.IPlanetService planetService;

    @MockBean
    private StarWarsApiConsumer starWarsApiConsumer;

    @MockBean
    private PlanetRepository planetRepository;

    @Test
    public void should_return_movies_appearances_quantity_when_find_by_right_name() {
        when(starWarsApiConsumer.getPlanetBy(RIGHT_PLANET_NAME)).thenReturn(rightPlanetResponseDTO);

        Integer quantity = planetService.getMoviesAppearancesQuantity(RIGHT_PLANET_NAME);
        assertEquals(EXPTECTED_QUANTITY_OF_APPEARANCES, quantity);
    }

    @Test
    public void should_return_0_movies_appearances_quantity_when_find_by_wrong_name() {
        when(starWarsApiConsumer.getPlanetBy(WRONG_PLANET_NAME)).thenReturn(wrongPlanetResponseDTO);

        Integer quantity = planetService.getMoviesAppearancesQuantity(WRONG_PLANET_NAME);
        assertEquals(EXPTECTED_QUANTITY_OF_APPEARANCES_WHEN_WRONG_NAME, quantity);
    }

    @Test
    public void should_return_all_filled_when_find()  {
        when(starWarsApiConsumer.getPlanetBy(RIGHT_PLANET_NAME)).thenReturn(rightPlanetResponseDTO);
        when(planetRepository.findAll()).thenReturn(Collections.singletonList(PLANET));

        List<PlanetDTO> planets = planetService.getAll();
        assertTrue(planets.size() > 0);
    }

    @Test
    public void should_return_all_empty_when_find()  {
        when(planetRepository.findAll()).thenReturn(Collections.emptyList());
        List<PlanetDTO> planets = planetService.getAll();
        assertTrue(planets.isEmpty());
    }

    @Test
    public void should_return_all_paginated_filled_when_find()  {
        when(starWarsApiConsumer.getPlanetBy(RIGHT_PLANET_NAME)).thenReturn(rightPlanetResponseDTO);
        when(planetRepository.findAll(PageRequest.of(RIGHT_PAGE, LENGHT, SORT)))
                .thenReturn(new PageImpl(Collections.singletonList(PLANET)));

        Page<PlanetDTO> planetDTOS = planetService.getAllPaginated(RIGHT_PAGE, "name", "ASC");
        assertFalse(planetDTOS.getContent().isEmpty());
    }

    @Test
    public void should_return_all_paginated_empty_when_find()  {
        when(planetRepository.findAll(PageRequest.of(WRONG_PAGE, LENGHT, SORT)))
                .thenReturn(Page.empty());

        Page<PlanetDTO> planetDTOS = planetService.getAllPaginated(WRONG_PAGE, "name", "ASC");
        assertTrue(planetDTOS.getContent().isEmpty());
    }

    @Test
    public void should_return_planet_dto_when_find_by_right_id()  {
        PLANET.setId(RIGHT_ID);
        when(starWarsApiConsumer.getPlanetBy(RIGHT_PLANET_NAME)).thenReturn(rightPlanetResponseDTO);
        when(planetRepository.findById(RIGHT_ID)).thenReturn(Optional.of(PLANET));

        PlanetDTO planetDTO = planetService.findById(RIGHT_ID);
        assertNotNull(planetDTO);
        assertEquals(PLANET.getId(), planetDTO.getId());
    }

    @Test
    public void should_return_planet_dto_when_find_by_wrong_id()  {
        WRONG_PLANET.setId(WRONG_ID);
        when(planetRepository.findById(WRONG_ID)).thenReturn(Optional.empty());

        PlanetDTO planetDTO = planetService.findById(WRONG_ID);
        assertNull(planetDTO);
    }

    @Test
    public void should_return_planet_dto_when_find_by_right_name()  {
        PLANET.setId(RIGHT_ID);
        when(starWarsApiConsumer.getPlanetBy(RIGHT_PLANET_NAME)).thenReturn(rightPlanetResponseDTO);
        when(planetRepository.findByName(RIGHT_PLANET_NAME)).thenReturn(PLANET);

        PlanetDTO planetDTO = planetService.findByName(RIGHT_PLANET_NAME);
        assertNotNull(planetDTO);
        assertEquals(PLANET.getId(), planetDTO.getId());
    }

    @Test
    public void should_return_planet_dto_when_find_by_wrong_name()  {
        WRONG_PLANET.setId(WRONG_ID);
        when(planetRepository.findByName(WRONG_PLANET_NAME)).thenReturn(null);

        PlanetDTO planetDTO = planetService.findById(WRONG_ID);
        assertNull(planetDTO);
    }

    @Test
    public void should_delete_when_call_right_id() {
        planetService.deleteById(RIGHT_ID);
        verify(planetRepository, Mockito.times(1)).deleteById(RIGHT_ID);
    }

    @Test
    public void should_return_true_when_exists_by_right_name() {
        when(planetRepository.findByNameOrId(RIGHT_PLANET_NAME, null)).thenReturn(PLANET);
        Map<String, Object> map = planetService.alreadyExists(RIGHT_PLANET_NAME, null);

        assertTrue((Boolean) map.get("alreadyExists"));
        assertNotNull(map.get("planet"));
    }

    @Test
    public void should_return_true_when_exists_by_right_id() {
        PLANET.setId(RIGHT_ID);
        when(planetRepository.findByNameOrId(null, RIGHT_ID)).thenReturn(PLANET);

        Map<String, Object> map = planetService.alreadyExists(null, RIGHT_ID);

        assertTrue((Boolean) map.get("alreadyExists"));
        assertNotNull(map.get("planet"));
    }

    @Test
    public void should_return_true_when_exists_by_wrong_name() {
        when(planetRepository.findByNameOrId(WRONG_PLANET_NAME, null)).thenReturn(null);
        Map<String, Object> map = planetService.alreadyExists(WRONG_PLANET_NAME, null);

        assertFalse((Boolean) map.get("alreadyExists"));
        assertNull(map.get("planet"));
    }

    @Test
    public void should_return_true_when_exists_by_wrong_id() {
        WRONG_PLANET.setId(WRONG_ID);
        when(planetRepository.findByNameOrId(null, WRONG_ID)).thenReturn(null);

        Map<String, Object> map = planetService.alreadyExists(null, WRONG_ID);

        assertFalse((Boolean) map.get("alreadyExists"));
        assertNull(map.get("planet"));
    }

}
