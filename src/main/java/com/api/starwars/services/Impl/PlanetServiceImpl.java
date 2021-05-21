package com.api.starwars.services.Impl;

import com.api.starwars.consumers.StarWarsApiConsumer;
import com.api.starwars.consumers.dtos.PlanetResponseDTO;
import com.api.starwars.domain.Planet;
import com.api.starwars.domain.dtos.PlanetDTO;
import com.api.starwars.repositories.PlanetRepository;
import com.api.starwars.services.PlanetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PlanetServiceImpl implements PlanetService {

    @Autowired
    StarWarsApiConsumer starWarsApiConsumer;
    @Autowired
    PlanetRepository planetRepository;
    @Value("${com.api.starwars.page_size.default}")
    private Integer pageSizeDefault;

    /**
     * Como a API usa um tipo de LIKE pra fazer a busca de planetas
     * vou assumir que a posição 0 é a mais próxima do que foi pedido
     */

    @Override
    public Integer getMoviesAppearancesQuantity(String planetName) throws IOException, InterruptedException {
        PlanetResponseDTO planets = starWarsApiConsumer.getPlanetBy(planetName);
        if (planets.getPlanetBodyDto().getCount() == 0) return 0;
        return planets.getPlanetBodyDto().getResults().get(0).getFilms().size();
    }

    @Override
    public List<PlanetDTO> getAll() throws IOException, InterruptedException {
        List<Planet> planets = planetRepository.findAll();
        List<PlanetDTO> planetsDTO = new ArrayList<>();

        for (Planet planet : planets) {
            planetsDTO.add(new PlanetDTO(planet, getMoviesAppearancesQuantity(planet.getName())));
        }

        return planetsDTO;
    }

    @Override
    public Page<PlanetDTO> getAllPaginated(Integer page, String order, String direction) throws IOException, InterruptedException {
        Sort sort = direction.equals("ASC") ? Sort.by(order).ascending() : Sort.by(order).descending();
        PageRequest pageRequest = PageRequest.of(page, pageSizeDefault, sort);

        List<PlanetDTO> planets = new ArrayList<>();
        for (Planet planet : planetRepository.findAll(pageRequest)) {
            planets.add(new PlanetDTO(planet, getMoviesAppearancesQuantity(planet.getName())));
        }

       return new PageImpl<>(planets);

    }

    @Override
    public PlanetDTO findById(String id) throws IOException, InterruptedException {
        Planet planet = planetRepository.findById(id).orElse(null);
        if(planet != null) return new PlanetDTO(planet, getMoviesAppearancesQuantity(planet.getName()));

        return null;
    }

    @Override
    public PlanetDTO findByName(String name) throws IOException, InterruptedException {
        Planet planet =  planetRepository.findByName(name);
        if(planet != null) return new PlanetDTO(planet, getMoviesAppearancesQuantity(planet.getName()));

        return null;
    }

    @Override
    public Planet save(Planet planet) {
        return planetRepository.save(planet);
    }

    @Override
    public void deleteById(String id) {
        planetRepository.deleteById(id);
    }

    @Override
    public Map<String, Object> alreadyExists(String name, String id) {
        Planet planet = planetRepository.findByNameOrId(name, id);
        Map<String, Object> map = new HashMap<>();

        map.put("alreadyExists", planet != null);
        map.put("planet", planet);

        return map;
    }

}
