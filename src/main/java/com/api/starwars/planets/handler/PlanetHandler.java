package com.api.starwars.planets.handler;

import com.api.starwars.commons.log.LoggerUtils;
import com.api.starwars.commons.response.PageResponse;
import com.api.starwars.planets.handler.interfaces.IPlanetService;
import com.api.starwars.planets.model.domain.Planet;
import com.api.starwars.planets.model.view.PlanetJson;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static com.api.starwars.planets.enums.OperationsEnum.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping(value = Constants.PLANETS_ENDPOINT)
@Tag(name = "Planets")
public class PlanetHandler {
    private final IPlanetService planetService;

    @Autowired
    public PlanetHandler(IPlanetService planetService) {
        this.planetService = planetService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PLANET_LIST')")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PageResponse.class)))
    public ResponseEntity<PageResponse<PlanetJson>> getAll(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "order", defaultValue = "name") String order,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "size", defaultValue = "15") Integer size,
            HttpServletRequest request) {
        log.info("Iniciando busca por todos os planetas. page: {}, order: {}, direction: {}, size: {}.", page, order,
                direction, size);
        LoggerUtils.setOperationInfoIntoMDC(GET_PLANETS_PAGE, request);

        Page<Planet> planets = planetService.findAll(page, order, direction, size);
        Page<PlanetJson> pageResponse = planets.map(PlanetJson::fromDomain);
        pageResponse = pageResponse
                .map(planet -> planet
                        .add(linkTo(methodOn(PlanetHandler.class).getByID(planet.getId(), 0L, null)).withSelfRel()))
                .map(planet -> planet.add(
                        linkTo(methodOn(PlanetHandler.class).getByName(planet.getName(), 0L, null)).withSelfRel()));

        log.info("Busca por todos os planetas concluida com sucesso. page: {}, order: {}, direction: {}, size: {}.",
                page, order, direction, size);
        return ResponseEntity.ok(PageResponse.fromPage(pageResponse));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PLANET_LIST')")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PlanetJson.class)))
    public ResponseEntity<PlanetJson> getByID(
            @PathVariable String id,
            @RequestParam(name = "cacheInDays", defaultValue = "0") Long cacheInDays,
            HttpServletRequest request) {
        log.info("Iniciando busca de planeta por id. id: {}. cacheInDays: {}.", id, cacheInDays);
        LoggerUtils.setOperationInfoIntoMDC(id, GET_PLANET_BY_ID, request);

        Planet planet = planetService.findById(id, cacheInDays);
        PlanetJson planetJson = PlanetJson.fromDomain(planet);
        planetJson.add(linkTo(methodOn(PlanetHandler.class).getAll(0, "name", "ASC", 15, null)).withSelfRel());
        planetJson.add(linkTo(methodOn(PlanetHandler.class).getByName(planet.name(), 0L, null)).withSelfRel());

        log.info("Busca de planeta por id concluida por sucesso. id: {}. cacheInDays: {}.", id, cacheInDays);
        return ResponseEntity.ok(planetJson);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('PLANET_LIST')")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PlanetJson.class)))
    public ResponseEntity<PlanetJson> getByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") Long cacheInDays,
            HttpServletRequest request) {
        log.info("Iniciando busca de planeta pelo nome. name: {}. cacheInDays: {}.", name, cacheInDays);
        LoggerUtils.setOperationInfoIntoMDC(name, GET_PLANET_BY_NAME, request);

        Planet planet = planetService.findByName(name, cacheInDays);
        PlanetJson planetJson = PlanetJson.fromDomain(planet);
        planetJson.add(linkTo(methodOn(PlanetHandler.class).getAll(0, "name", "ASC", 15, null)).withSelfRel());
        planetJson.add(linkTo(methodOn(PlanetHandler.class).getByID(planet.id(), 0L, null)).withSelfRel());

        log.info("Busca de planeta pelo nome concluida com sucesso. name: {}. cacheInDays: {}.", name, cacheInDays);
        return ResponseEntity.ok(planetJson);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('PLANET_UPDATE')")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PlanetJson.class)))
    public ResponseEntity<PlanetJson> updateById(@RequestBody PlanetJson planetJson, HttpServletRequest request) {
        log.info("Iniciando atualizacao de planeta pelo id. id: {}", planetJson.getId());
        LoggerUtils.setOperationInfoIntoMDC(planetJson.getId(), UPDATE_PLANET, request);

        Planet planet = planetService.updateById(planetJson.getId(), planetJson.toDomain());
        planetJson.add(linkTo(methodOn(PlanetHandler.class).getByID(planet.id(), 0L, null)).withSelfRel());
        planetJson.add(linkTo(methodOn(PlanetHandler.class).getByName(planet.name(), 0L, null)).withSelfRel());

        log.info("Planeta atualizado pelo id com sucesso. id: {}", planetJson.getId());
        return ResponseEntity.ok(PlanetJson.fromDomain(planet));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PLANET_CREATE')")
    @ApiResponse(responseCode = "201", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PlanetJson.class)))
    public ResponseEntity<PlanetJson> post(@Valid @RequestBody PlanetJson planet, HttpServletRequest request) {
        log.info("Iniciando cadastro de planeta por nome. name: {}.", planet.getName());
        LoggerUtils.setOperationInfoIntoMDC(planet.getId(), DELETE_PLANET, request);

        Planet domainPlanet = planetService.save(planet.toDomain());
        PlanetJson planetJson = PlanetJson.fromDomain(domainPlanet);
        planetJson.add(linkTo(methodOn(PlanetHandler.class).getByID(domainPlanet.id(), 0L, null)).withSelfRel());
        planetJson.add(linkTo(methodOn(PlanetHandler.class).getByName(domainPlanet.name(), 0L, null)).withSelfRel());

        log.info("Cadastro de planeta por nome concluido. id {}. name: {}.", planetJson.getId(), planet.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(planetJson);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PLANET_DELETE')")
    @ApiResponse(responseCode = "204", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    public ResponseEntity<?> delete(@PathVariable String id, HttpServletRequest request) {
        log.info("Iniciando exclusao de planeta pelo id: {}.", id);
        LoggerUtils.setOperationInfoIntoMDC(id, DELETE_PLANET, request);

        planetService.deleteById(id);

        log.info("Exclusao de planeta pelo id concluida: {}.", id);
        return ResponseEntity.noContent().build();
    }
}
