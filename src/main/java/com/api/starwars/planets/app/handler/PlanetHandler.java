package com.api.starwars.planets.app.handler;

import com.api.starwars.infra.log.LoggerUtils;
import com.api.starwars.planets.domain.model.Planet;
import com.api.starwars.planets.domain.model.view.PlanetView;
import com.api.starwars.planets.domain.operations.PlanetOperations;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;

import static com.api.starwars.planets.app.handler.OperationsEnum.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@Tag(name = "Planets")
@RequestMapping(value = Constants.PLANETS_ENDPOINT)
public class PlanetHandler {
    private final PlanetOperations planetService;

    @Autowired
    public PlanetHandler(PlanetOperations planetService) {
        this.planetService = planetService;
    }

    @GetMapping
    //@PreAuthorize("hasAuthority('PLANET_LIST')")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PlanetView.class)))
    public Flux<PlanetView> getAll(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "15") Integer size,
            HttpServletRequest request) {
        log.info("Iniciando busca por todos os planetas. page: {}, size: {}.", page, size);
        LoggerUtils.setOperationInfoIntoMDC(GET_PLANETS_PAGE, request);

        return planetService.findAll(page, size)
                .flatMap(planet -> {
                    PlanetView planetView = PlanetView.fromDomain(planet);
                    planetView.add(linkTo(methodOn(PlanetHandler.class).getByID(planet.id(), 0L, null)).withSelfRel());
                    return Mono.just(planetView);
                })
                .doOnNext(planetView -> log.info("Busca por todos os planetas concluida com sucesso. page: {}, size: {}.", page, size));
    }

    @GetMapping("/{id}")
    //@PreAuthorize("hasAuthority('PLANET_LIST')")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PlanetView.class)))
    public Mono<PlanetView> getByID(
            @PathVariable String id,
            @RequestParam(name = "cacheInDays", defaultValue = "0") Long cacheInDays,
            HttpServletRequest request) {
        log.info("Iniciando busca de planeta por id. id: {}. cacheInDays: {}.", id, cacheInDays);
        LoggerUtils.setOperationInfoIntoMDC(id, GET_PLANET_BY_ID, request);

        return planetService.findById(id, cacheInDays)
                .flatMap(tempPlanet -> {
                    PlanetView planetView = PlanetView.fromDomain(tempPlanet);
                    planetView.add(linkTo(methodOn(PlanetHandler.class).getAll(0, 0, null)).withSelfRel());
                    planetView.add(linkTo(methodOn(PlanetHandler.class).getByName(tempPlanet.name(), 0L, null)).withSelfRel());
                    return Mono.just(planetView);
                })
                .doOnSuccess(planetView -> log.info("Busca de planeta por id concluida com sucesso. id: {}. cacheInDays: {}.", id, cacheInDays));
    }

    @GetMapping("/search")
    //@PreAuthorize("hasAuthority('PLANET_LIST')")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PlanetView.class)))
    public Mono<PlanetView> getByName(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "0") Long cacheInDays,
            HttpServletRequest request) {
        log.info("Iniciando busca de planeta pelo nome. name: {}. cacheInDays: {}.", name, cacheInDays);
        LoggerUtils.setOperationInfoIntoMDC(name, GET_PLANET_BY_NAME, request);

        return planetService.findByName(name, cacheInDays)
                .flatMap(tempPlanet -> {
                    PlanetView planetView = PlanetView.fromDomain(tempPlanet);
                    planetView.add(linkTo(methodOn(PlanetHandler.class).getAll(0, 0, null)).withSelfRel());
                    planetView.add(linkTo(methodOn(PlanetHandler.class).getByID(tempPlanet.id(), 0L, null)).withSelfRel());
                    return Mono.just(planetView);
                })
                .doOnSuccess(planet1 -> log.info("Busca de planeta pelo nome concluida com sucesso. name: {}. cacheInDays: {}.", name, cacheInDays));

    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    //@PreAuthorize("hasAuthority('PLANET_UPDATE')")
    @ApiResponse(responseCode = "204", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PlanetView.class)))
    public Mono<PlanetView> updateById(
            @PathVariable String id,
            @RequestBody PlanetView planetView,
            HttpServletRequest request
    ) {
        log.info("Iniciando atualizacao de planeta pelo id. id: {}", id);
        LoggerUtils.setOperationInfoIntoMDC(id, UPDATE_PLANET, request);

        return planetService.updateById(id, planetView.toDomain())
                .doOnSuccess(planet -> log.info("Atualizacao de planeta pelo id concluida com sucesso. id: {}", id))
                .then(Mono.empty());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    //@PreAuthorize("hasAuthority('PLANET_CREATE')")
    @ApiResponse(responseCode = "201", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PlanetView.class)))
    public Mono<PlanetView> post(@RequestBody PlanetView planet, HttpServletRequest request) {
        log.info("Iniciando cadastro de planeta por nome. name: {}.", planet.getName());
        LoggerUtils.setOperationInfoIntoMDC(planet.getId(), DELETE_PLANET, request);

        return planetService.save(planet.toDomain())
                .flatMap(tempPlanet -> {
                    PlanetView planetView = PlanetView.fromDomain(tempPlanet);
                    planetView.add(linkTo(methodOn(PlanetHandler.class).getAll(0, 0, null)).withSelfRel());
                    planetView.add(linkTo(methodOn(PlanetHandler.class).getByID(tempPlanet.id(), 0L, null)).withSelfRel());
                    planetView.add(linkTo(methodOn(PlanetHandler.class).getByName(tempPlanet.name(), 0L, null)).withSelfRel());
                    return Mono.just(planetView);
                })
                .doOnNext(planetView -> log.info("Cadastro de planeta por nome concluido com sucesso. name: {}.", planet.getName()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    //@PreAuthorize("hasAuthority('PLANET_DELETE')")
    @ApiResponse(responseCode = "204", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    public Mono<Planet> delete(@PathVariable String id, HttpServletRequest request) {
        log.info("Iniciando exclusao de planeta pelo id: {}.", id);
        LoggerUtils.setOperationInfoIntoMDC(id, DELETE_PLANET, request);

        return planetService.deleteById(id)
                .doOnSuccess(planet -> log.info("Exclusao de planeta pelo id concluida com sucesso. id: {}.", id))
                .then(Mono.empty());
    }
}
