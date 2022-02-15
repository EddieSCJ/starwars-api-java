package com.api.starwars.planets.handlers;

import com.api.starwars.commons.response.PageResponse;
import com.api.starwars.planets.model.view.PlanetJson;
import com.api.starwars.planets.services.IPlanetService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.api.starwars.planets.util.EndpointConstants.*;

@Slf4j
@RestController
@RequestMapping(API + PLANET)
//TODO use Tags instead of description
@Api(description = "Planets Endpoint", tags = "Planets")
public class PlanetHandler {

    private final IPlanetService planetService;

    @Autowired
    public PlanetHandler(IPlanetService planetService) {
        this.planetService = planetService;
    }

    @GetMapping
    public ResponseEntity<PageResponse> getAll(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "order", defaultValue = "name") String order,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "size", defaultValue = "15") Integer size
    ) throws Exception {
        log.info("Iniciando busca por todos os planetas. page: {}, order: {}, direction: {}, size: {}.", page, order, direction, size);
        Page<com.api.starwars.planets.model.domain.Planet> planets = planetService.findAll(page, order, direction, size);
        Page<PlanetJson> pageResponse = planets.map(PlanetJson::fromDomain);

        log.info("Busca por todos os planetas concluida com sucesso. page: {}, order: {}, direction: {}, size: {}.", page, order, direction, size);
        return ResponseEntity.ok(PageResponse.fromPage(pageResponse));
    }

    @GetMapping(ID)
    public ResponseEntity<PlanetJson> getByID(
            @PathVariable String id,
            @RequestParam(name = "cacheInDays", defaultValue = "0") Long cacheInDays
    ) throws Exception {
        log.info("Iniciando busca de planeta por id. id: {}. cacheInDays: {}.", id, cacheInDays);
        com.api.starwars.planets.model.domain.Planet planet = planetService.findById(id, cacheInDays);
        PlanetJson planetJson = PlanetJson.fromDomain(planet);

        log.info("Busca de planeta por id concluida por sucesso. id: {}. cacheInDays: {}.", id, cacheInDays);
        return ResponseEntity.ok(planetJson);
    }

    @GetMapping(NAME)
    public ResponseEntity<PlanetJson> getByName(
            @PathVariable String name,
            @RequestParam(defaultValue = "0") Long cacheInDays
    ) throws Exception {
        log.info("Iniciando busca de planeta pelo nome. name: {}. cacheInDays: {}.", name, cacheInDays);
        com.api.starwars.planets.model.domain.Planet planet = planetService.findByName(name, cacheInDays);
        PlanetJson planetJson = PlanetJson.fromDomain(planet);

        log.info("Busca de planeta pelo nome concluida com sucesso. name: {}. cacheInDays: {}.", name, cacheInDays);
        return ResponseEntity.ok(planetJson);
    }

    @PostMapping
    public ResponseEntity<PlanetJson> post(@Valid PlanetJson planet) {
        log.info("Iniciando cadastro de planeta por nome. name: {}.", planet.getName());
        com.api.starwars.planets.model.domain.Planet domainPlanet = planetService.save(planet.toDomain());
        PlanetJson planetJson = PlanetJson.fromDomain(domainPlanet);

        log.info("Cadastro de planeta por nome concluida. id {}. name: {}.", planetJson.getId(), planet.getName());
        return ResponseEntity.ok(planetJson);
    }

    @DeleteMapping(ID)
    public ResponseEntity<?> delete(@PathVariable String id) {
        log.info("Iniciando exclusao de planeta pelo id: {}.", id);
        planetService.deleteById(id);

        log.info("Exclusao de planeta pelo id concluida: {}.", id);
        return ResponseEntity.noContent().build();
    }
}
