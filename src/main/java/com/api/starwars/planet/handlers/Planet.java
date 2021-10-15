package com.api.starwars.handlers;

import com.api.starwars.planet.model.view.PlanetJson;
import com.api.starwars.response.Response;
import com.api.starwars.services.planets.IPlanetService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static com.api.starwars.planet.util.EndpointConstants.*;
import static com.api.starwars.helpers.ErrorMessageHelper.ERROR_WITH_FILMS_QUANTITY_APPEARANCE;

@RestController()
@RequestMapping(API + PLANET)
//TODO use Tags instead of description
@Api(description = "Planets Endpoint", tags = "Planets")
public class Planet {

    @Autowired
    IPlanetService planetService;

    @GetMapping
    public ResponseEntity<Response<List<PlanetJson>>> getAll() {
        Response<List<PlanetJson>> response = new Response<>();
        try {
            response.setResult(planetService.getAll());
        } catch (Exception e) {
            handleAppearancesApiError(e, response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(PAGINATED)
    public ResponseEntity<Response<Page<PlanetJson>>> getAllPaginated(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "order", defaultValue = "name") String order,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction
    ) {

        Response<Page<PlanetJson>> response = new Response<>();
        try {
            response.setResult(planetService.getAllPaginated(page, order, direction));
        } catch (Exception e) {
            handleAppearancesApiError(e, response);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping(ID)
    public ResponseEntity<Response<PlanetJson>> getByID(@PathVariable String id) {
        Response<PlanetJson> response = genericGet(null, id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping(NAME)
    public ResponseEntity<Response<PlanetJson>> getByName(@PathVariable String name) {
        Response<PlanetJson> response = genericGet(name, null);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping
    public ResponseEntity<Response<com.api.starwars.planet.model.domain.Planet>> post(@Valid @RequestBody com.api.starwars.planet.model.domain.Planet planet, BindingResult result) {
        Boolean alreadyExists = (Boolean) planetService.alreadyExists(planet.getName(), null).get("alreadyExists");
        Response<com.api.starwars.planet.model.domain.Planet> response = getValidatedResponseInstance(alreadyExists, false, result);

        if (response.getErrors().isEmpty()) response.setResult(planetService.save(planet));

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping(ID)
    public ResponseEntity<Response<String>> delete(@PathVariable String id) {
        Map<String, Object> result = planetService.alreadyExists(null, id);
        Boolean alreadyExists = (Boolean) result.get("alreadyExists");
        Response<String> response = getValidatedResponseInstance(alreadyExists, true, null);

        if(response.getErrors().isEmpty()) {
            planetService.deleteById(id);
            response.setResult("Deletado com sucesso");
        }

        return ResponseEntity.status(response.getStatus()).body(response);
    }


    private Response<PlanetJson> genericGet(String name, String id) {
        Map<String, Object> result = planetService.alreadyExists(name, id);
        Boolean alreadyExists = (Boolean) result.get("alreadyExists");

        Response<PlanetJson> response = getValidatedResponseInstance(alreadyExists, true, null);
        if (response.getErrors().isEmpty()) {
            try {
                com.api.starwars.planet.model.domain.Planet planet = (com.api.starwars.planet.model.domain.Planet) result.get("planet");
                PlanetJson planetDto = new PlanetJson(planet, planetService.getMoviesAppearancesQuantity(planet.getName()));
                response.setResult(planetDto);
            } catch (Exception e) {
               handleAppearancesApiError(e, response);
            }
        }
        return response;
    }

    private void handleAppearancesApiError(Exception e, Response<?> response) {
        e.printStackTrace();
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.getErrors().add(ERROR_WITH_FILMS_QUANTITY_APPEARANCE);
    }
}
