package com.api.starwars.domain.planets.handlers;

import com.api.starwars.domain.planets.model.mongo.MongoPlanet;
import com.api.starwars.domain.planets.model.view.PlanetJson;
import com.api.starwars.domain.planets.repositories.IPlanetMongoRepository;
import com.api.starwars.domain.planets.repositories.IPlanetRepository;
import commons.base.AbstractIntegrationTest;
import commons.utils.DomainUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PlanetHandlerTest extends AbstractIntegrationTest {

    @Autowired
    IPlanetRepository planetRepository;

    @Autowired
    IPlanetMongoRepository planetMongoRepository;

    @BeforeEach
    public void setup() {
        this.planetMongoRepository.deleteAll();
    }

    public MongoPlanet saveMongoPlanet() {
        MongoPlanet mongoPlanet = DomainUtils.getRandomMongoPlanet();
        return planetMongoRepository.save(mongoPlanet);
    }

    @Nested
    class GetAll {
        private static final String ENDPOINT = "/planets";

        @Test
        @DisplayName("Deve retornar 10 planetas buscados na API de star wars com sucesso devido a base estar vazia.")
        public void find_in_star_wars_api_successful() throws Exception {
            mockMvc.perform(get(ENDPOINT))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.page").value(0))
                    .andExpect(jsonPath("$.size").value(15))
                    .andExpect(jsonPath("$.totalPages").value(1))
                    .andExpect(jsonPath("$.totalElements").value(10));
        }

        @Test
        @DisplayName("Deve retornar 1 planeta buscado na base de dados com sucesso.")
        public void find_in_database_successfully() throws Exception {
            PlanetJson planet = PlanetJson.fromDomain(saveMongoPlanet().toDomain());
            mockMvc.perform(get(ENDPOINT))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.page").value(0))
                    .andExpect(jsonPath("$.size").value(15))
                    .andExpect(jsonPath("$.totalPages").value(1))
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.result[0].id").value(planet.getId()))
                    .andExpect(jsonPath("$.result[0].name").value(planet.getName()))
                    .andExpect(jsonPath("$.result[0].cacheInDays").value(planet.getCacheInDays()))
                    .andExpect(jsonPath("$.result[0].movieAppearances").value(planet.getMovieAppearances()));
        }

        @Test
        @DisplayName("Deve retornar apenas o tamanho definido no size com sucesso.")
        public void find_with_parameter() throws Exception {
            mockMvc.perform(get(ENDPOINT).queryParam("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size").value(5));
        }
    }
}
