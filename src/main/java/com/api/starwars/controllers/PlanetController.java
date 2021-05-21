package com.api.starwars.controllers;

import com.api.starwars.domain.Planet;
import com.api.starwars.domain.dtos.PlanetDTO;
import com.api.starwars.response.Response;
import com.api.starwars.services.PlanetService;
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

import static com.api.starwars.helpers.ApiHelper.*;
import static com.api.starwars.helpers.ErrorMessageHelper.ERROR_WITH_FILMS_QUANTITY_APPEARANCE;

@RestController()
@RequestMapping(API + PLANET)
@Api(description = "Planets Endpoint", tags = "Planets")
public class PlanetController {

    @Autowired
    PlanetService planetService;

    @GetMapping
    public ResponseEntity<Response<List<PlanetDTO>>> getAll() {
        Response<List<PlanetDTO>> response = new Response<>();
        try {
            response.setResult(planetService.getAll());
        } catch (Exception e) {
            handleAppearancesApiError(e, response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(PAGINATED)
    public ResponseEntity<Response<Page<PlanetDTO>>> getAllPaginated(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "order", defaultValue = "name") String order,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction
    ) {

        Response<Page<PlanetDTO>> response = new Response<>();
        try {
            response.setResult(planetService.getAllPaginated(page, order, direction));
        } catch (Exception e) {
            handleAppearancesApiError(e, response);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping(ID)
    public ResponseEntity<Response<PlanetDTO>> getByID(@PathVariable String id) {
        Response<PlanetDTO> response = genericGet(null, id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping(NAME)
    public ResponseEntity<Response<PlanetDTO>> getByName(@PathVariable String name) {
        Response<PlanetDTO> response = genericGet(name, null);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping
    public ResponseEntity<Response<Planet>> post(@Valid @RequestBody Planet planet, BindingResult result) {
        Boolean alreadyExists = (Boolean) planetService.alreadyExists(planet.getName(), null).get("alreadyExists");
        Response<Planet> response = getValidatedResponseInstance(alreadyExists, false, result);

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


    private Response<PlanetDTO> genericGet(String name, String id) {
        Map<String, Object> result = planetService.alreadyExists(name, id);
        Boolean alreadyExists = (Boolean) result.get("alreadyExists");

        Response<PlanetDTO> response = getValidatedResponseInstance(alreadyExists, true, null);
        if (response.getErrors().isEmpty()) {
            try {
                Planet planet = (Planet) result.get("planet");
                PlanetDTO planetDto = new PlanetDTO(planet, planetService.getMoviesAppearancesQuantity(planet.getName()));
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
