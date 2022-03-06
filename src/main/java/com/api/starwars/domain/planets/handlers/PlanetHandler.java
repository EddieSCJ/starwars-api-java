package com.api.starwars.domain.planets.handlers;

import com.api.starwars.commons.response.PageResponse;
import com.api.starwars.domain.planets.model.domain.Planet;
import com.api.starwars.domain.planets.model.view.PlanetJson;
import com.api.starwars.domain.planets.services.IPlanetService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping(value = "/planets")
@Tag(name = "Planets")
public class PlanetHandler {

    private final IPlanetService planetService;

    @Autowired
    public PlanetHandler(IPlanetService planetService) {
        this.planetService = planetService;
    }

    @GetMapping
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PageResponse.class)))
    public ResponseEntity<PageResponse<PlanetJson>> getAll(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "order", defaultValue = "name") String order,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "size", defaultValue = "15") Integer size
    ) {
        log.info("Iniciando busca por todos os planetas. page: {}, order: {}, direction: {}, size: {}.", page, order, direction, size);
        Page<Planet> planets = planetService.findAll(page, order, direction, size);
        Page<PlanetJson> pageResponse = planets.map(PlanetJson::fromDomain);

        log.info("Busca por todos os planetas concluida com sucesso. page: {}, order: {}, direction: {}, size: {}.", page, order, direction, size);
        return ResponseEntity.ok(PageResponse.fromPage(pageResponse));
    }

    @GetMapping("/{id}")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PlanetJson.class)))
    public ResponseEntity<PlanetJson> getByID(
            @PathVariable String id,
            @RequestParam(name = "cacheInDays", defaultValue = "0") Long cacheInDays
    ) throws IOException, InterruptedException {
        log.info("Iniciando busca de planeta por id. id: {}. cacheInDays: {}.", id, cacheInDays);
        Planet planet = planetService.findById(id, cacheInDays);
        PlanetJson planetJson = PlanetJson.fromDomain(planet);

        log.info("Busca de planeta por id concluida por sucesso. id: {}. cacheInDays: {}.", id, cacheInDays);
        return ResponseEntity.ok(planetJson);
    }

    @GetMapping("/search")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PlanetJson.class)))
    public ResponseEntity<PlanetJson> getByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") Long cacheInDays
    ) throws IOException, InterruptedException {
        log.info("Iniciando busca de planeta pelo nome. name: {}. cacheInDays: {}.", name, cacheInDays);
        Planet planet = planetService.findByName(name, cacheInDays);
        PlanetJson planetJson = PlanetJson.fromDomain(planet);

        log.info("Busca de planeta pelo nome concluida com sucesso. name: {}. cacheInDays: {}.", name, cacheInDays);
        return ResponseEntity.ok(planetJson);
    }

    @PutMapping
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PlanetJson.class)))
    public ResponseEntity<PlanetJson> updateById(@RequestBody PlanetJson planetJson) {
        String id = planetJson.getId();
        log.info("Iniciando atualizacao de planeta pelo id. id: {}", id);
        Planet planet = planetService.updateById(id, planetJson.toDomain());

        log.info("Planeta atualizado pelo id com sucesso. id: {}", id);
        return ResponseEntity.ok(PlanetJson.fromDomain(planet));
    }

    @PostMapping
    @ApiResponse(responseCode = "201", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PlanetJson.class)))
    public ResponseEntity<PlanetJson> post(@Valid @RequestBody PlanetJson planet) {
        log.info("Iniciando cadastro de planeta por nome. name: {}.", planet.getName());

        Planet domainPlanet = planetService.save(planet.toDomain());
        PlanetJson planetJson = PlanetJson.fromDomain(domainPlanet);

        log.info("Cadastro de planeta por nome concluido. id {}. name: {}.", planetJson.getId(), planet.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(planetJson);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    public ResponseEntity<?> delete(@PathVariable String id) {
        log.info("Iniciando exclusao de planeta pelo id: {}.", id);
        planetService.deleteById(id);

        log.info("Exclusao de planeta pelo id concluida: {}.", id);
        return ResponseEntity.noContent().build();
    }
}
