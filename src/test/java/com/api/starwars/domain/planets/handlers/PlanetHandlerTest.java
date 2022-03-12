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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static java.text.MessageFormat.format;
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
        void find_in_star_wars_api_successful() throws Exception {
            mockMvc.perform(get(ENDPOINT))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.page").value(0))
                    .andExpect(jsonPath("$.size").value(15))
                    .andExpect(jsonPath("$.totalPages").value(1))
                    .andExpect(jsonPath("$.totalElements").value(10));
        }

        @Test
        @DisplayName("Deve retornar 1 planeta buscado na base de dados com sucesso.")
        void find_in_database_successfully() throws Exception {
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
        void find_with_parameter() throws Exception {
            mockMvc.perform(get(ENDPOINT).queryParam("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size").value(5));
        }
    }

    @Nested
    class getById {

        private static final String ENDPOINT = "/planets/";

        @Test
        @DisplayName("Deve retornar um planeta buscado na base de dados com sucesso.")
        void find_in_database_successful() throws Exception {
            MongoPlanet mongoPlanet = saveMongoPlanet();
            mockMvc.perform(get(ENDPOINT + mongoPlanet.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(mongoPlanet.getId()))
                    .andExpect(jsonPath("$.name").value(mongoPlanet.getName()))
                    .andExpect(jsonPath("$.cacheInDays").value(0L))
                    .andExpect(jsonPath("$.movieAppearances").value(mongoPlanet.getMovieAppearances()));
        }

        @Test
        @DisplayName("Deve buscar com sucesso um planeta na API de star wars quando o cache for invalido.")
        void find_in_star_wars_api_successful() throws Exception {
            MongoPlanet mongoPlanet = saveMongoPlanet();
            MongoPlanet newMongoPlanet = MongoPlanet.builder()
                    .id(mongoPlanet.getId())
                    .name("Tatooine")
                    .climate(mongoPlanet.getClimate())
                    .terrain(mongoPlanet.getTerrain())
                    .movieAppearances(mongoPlanet.getMovieAppearances())
                    .build();

            mockMvc.perform(get(ENDPOINT + mongoPlanet.getId())
                            .queryParam("cacheInDays", "0"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(mongoPlanet.getId()))
                    .andExpect(jsonPath("$.name").value(mongoPlanet.getName()))
                    .andExpect(jsonPath("$.cacheInDays").value(0L));
        }

        @Test
        @DisplayName("Deve estourar 404 quando nao encontrar um planeta por id na base de dados.")
        void throw_not_found() throws Exception {
            final String id = UUID.randomUUID().toString();
            mockMvc.perform(get(ENDPOINT + id))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(format("Nenhum planeta com id {0} foi encontrado.", id)));

        }

        @Test
        @DisplayName("Deve estourar 404 quando nao encontrar um planeta na API de star wars.")
        void throw_not_found_when_not_found_in_starwars_api() throws Exception {
            MongoPlanet mongoPlanet = saveMongoPlanet();
            mongoPlanet.setCreationDate(LocalDateTime.now().minus(3, ChronoUnit.DAYS));

            MongoPlanet invalidCacheMongoPlanet = planetMongoRepository.save(mongoPlanet);

            mockMvc.perform(get(ENDPOINT + invalidCacheMongoPlanet.getId())
                            .queryParam("cacheInDays", "0"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(format("Planeta com nome {0} não encontrado.", invalidCacheMongoPlanet.getName())));
        }

    }

}
