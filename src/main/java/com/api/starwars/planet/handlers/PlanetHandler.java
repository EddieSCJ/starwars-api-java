package com.api.starwars.planet.handlers;

import com.api.starwars.planet.model.domain.Planet;
import com.api.starwars.planet.model.mongo.MongoPlanet;
import com.api.starwars.planet.model.view.PlanetJson;
import com.api.starwars.planet.services.IPlanetService;
import com.api.starwars.response.Response;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.api.starwars.planet.util.EndpointConstants.*;

@RestController
@RequestMapping(API + PLANET)
//TODO use Tags instead of description
@Api(description = "Planets Endpoint", tags = "Planets")
public class PlanetHandler {

    @Autowired
    IPlanetService planetService;

    @GetMapping
    public ResponseEntity<Response<Page<PlanetJson>>> findAll(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "order", defaultValue = "name") String order,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "size", defaultValue = "15") Integer size
    ) throws IOException, InterruptedException {

        Page<Planet> planets = planetService.findAll(page, order, direction, size);
        List<PlanetJson> planetJsonList = planets.getContent().parallelStream().map(PlanetJson::fromDomain)
                .collect(Collectors.toList());

        Response<Page<PlanetJson>> response = new Response<>();
        Page<PlanetJson> pageResponse = new PageImpl<>(planetJsonList, planets.getPageable(), planets.getTotalElements());

        response.setResult(pageResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping(ID)
    public ResponseEntity<Response<PlanetJson>> getByID(
            @PathVariable String id,
            @RequestParam(name = "cacheInDays", defaultValue = "0") Long cacheInDays
    ) throws Exception {
        Planet planet = planetService.findById(id, cacheInDays);

        Response<PlanetJson> response = new Response<>();
        response.setResult(PlanetJson.fromDomain(planet));

        return ResponseEntity.ok(response);
    }

    @GetMapping(NAME)
    public ResponseEntity<Response<PlanetJson>> getByName(
            @PathVariable String name,
            @RequestParam(name = "cacheInDays", defaultValue = "0") Long cacheInDays
    ) throws Exception {
        Planet planet = planetService.findByName(name, cacheInDays);

        Response<PlanetJson> response = new Response<>();
        response.setResult(PlanetJson.fromDomain(planet));

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Response<PlanetJson>> post(@Valid PlanetJson planet, BindingResult result) {
        MongoPlanet mongoPlanet = planetService.save(planet.toDomain());
        Response<PlanetJson> response = new Response<>();

        response.setResult(PlanetJson.fromDomain(mongoPlanet.toDomain()));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(ID)
    public ResponseEntity<Response<String>> delete(@PathVariable String id) {
        planetService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}
