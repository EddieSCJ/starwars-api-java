package com.api.starwars.planet.handlers;

import com.api.commons.response.PageResponse;
import com.api.starwars.planet.model.domain.Planet;
import com.api.starwars.planet.model.mongo.MongoPlanet;
import com.api.starwars.planet.model.view.PlanetJson;
import com.api.starwars.planet.services.IPlanetService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

import static com.api.starwars.planet.util.EndpointConstants.*;

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
    ) throws IOException, InterruptedException {
        Page<Planet> planets = planetService.findAll(page, order, direction, size);
        Page<PlanetJson> pageResponse = planets.map(PlanetJson::fromDomain);

        return ResponseEntity.ok(PageResponse.fromPage(pageResponse));
    }

    @GetMapping(ID)
    public ResponseEntity<PlanetJson> getByID(
            @PathVariable String id,
            @RequestParam(name = "cacheInDays", defaultValue = "0") Long cacheInDays
    ) throws Exception {
        Planet planet = planetService.findById(id, cacheInDays);
        PlanetJson planetJson = PlanetJson.fromDomain(planet);

        return ResponseEntity.ok(planetJson);
    }

    @GetMapping(NAME)
    public ResponseEntity<PlanetJson> getByName(
            @PathVariable String name,
            @RequestParam(defaultValue = "0") Long cacheInDays
    ) throws Exception {
        Planet planet = planetService.findByName(name, cacheInDays);
        PlanetJson planetJson = PlanetJson.fromDomain(planet);

        return ResponseEntity.ok(planetJson);
    }

    @PostMapping
    public ResponseEntity<PlanetJson> post(@Valid PlanetJson planet) {
        MongoPlanet mongoPlanet = planetService.save(planet.toDomain());
        PlanetJson planetJson = PlanetJson.fromDomain(mongoPlanet.toDomain());

        return ResponseEntity.ok(planetJson);
    }

    @DeleteMapping(ID)
    public ResponseEntity<?> delete(@PathVariable String id) {
        planetService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
