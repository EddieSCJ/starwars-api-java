package com.api.starwars.planets.handler;

import com.api.starwars.planets.model.mongo.MongoPlanet;
import com.api.starwars.planets.model.view.PlanetJson;
import com.api.starwars.planets.services.interfaces.IPlanetMongoRepository;
import com.api.starwars.planets.services.interfaces.IPlanetRepository;
import commons.base.AbstractIntegrationTest;
import commons.utils.DomainUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.github.dockerjava.core.MediaType;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static java.text.MessageFormat.format;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PlanetHandlerTest extends AbstractIntegrationTest {

    @Autowired
    IPlanetRepository planetRepository;

    @Autowired
    IPlanetMongoRepository planetMongoRepository;

    @BeforeEach
    void setup() {
        this.planetMongoRepository.deleteAll();
    }

    private MongoPlanet saveMongoPlanet(MongoPlanet mongoPlanet) {
        if (mongoPlanet == null)
            mongoPlanet = DomainUtils.getRandomMongoPlanet();

        return planetMongoRepository.save(mongoPlanet);
    }

    @Nested
    class GetAll {

        @Test
        @DisplayName("Deve retornar 10 planetas buscados na API de star wars com sucesso devido a base estar vazia.")
        void find_in_star_wars_api_successful() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get(Constants.PLANETS_ENDPOINT))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.page").value(0))
                    .andExpect(jsonPath("$.size").value(15))
                    .andExpect(jsonPath("$.totalPages").value(1))
                    .andExpect(jsonPath("$.totalElements").value(10));
        }

        @Test
        @DisplayName("Deve retornar 1 planeta buscado na base de dados com sucesso.")
        void find_in_database_successfully() throws Exception {
            MongoPlanet mongoPlanet = saveMongoPlanet(DomainUtils.getRandomMongoPlanet());
            PlanetJson planet = PlanetJson.fromDomain(mongoPlanet.toDomain());
            mockMvc.perform(MockMvcRequestBuilders.get(Constants.PLANETS_ENDPOINT))
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
            mockMvc.perform(MockMvcRequestBuilders.get(Constants.PLANETS_ENDPOINT).queryParam("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size").value(5));
        }
    }

    @Nested
    class GetById {


        @Test
        @DisplayName("Deve retornar um planeta buscado na base de dados com sucesso.")
        void find_in_database_successful() throws Exception {
            MongoPlanet mongoPlanet = saveMongoPlanet(DomainUtils.getRandomMongoPlanet());
            mockMvc.perform(get(format("{0}/{1}", Constants.PLANETS_ENDPOINT, mongoPlanet.getId())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(mongoPlanet.getId()))
                    .andExpect(jsonPath("$.name").value(mongoPlanet.getName()))
                    .andExpect(jsonPath("$.cacheInDays").value(0L))
                    .andExpect(jsonPath("$.movieAppearances").value(mongoPlanet.getMovieAppearances()));
        }

        @Test
        @DisplayName("Deve buscar com sucesso um planeta na API de star wars quando o cache for invalido.")
        void find_in_star_wars_api_successful() throws Exception {
            MongoPlanet mongoPlanet = DomainUtils.getRandomMongoPlanet();
            MongoPlanet newMongoPlanet = MongoPlanet.builder()
                    .id(mongoPlanet.getId())
                    .name("Tatooine")
                    .climate(mongoPlanet.getClimate())
                    .terrain(mongoPlanet.getTerrain())
                    .movieAppearances(mongoPlanet.getMovieAppearances())
                    .creationDate(LocalDateTime.now().minus(2, ChronoUnit.DAYS))
                    .build();

            saveMongoPlanet(newMongoPlanet);

            mockMvc.perform(get(format("{0}/{1}", Constants.PLANETS_ENDPOINT, mongoPlanet.getId()))
                            .queryParam("cacheInDays", "0"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(newMongoPlanet.getId()))
                    .andExpect(jsonPath("$.name").value(newMongoPlanet.getName()))
                    .andExpect(jsonPath("$.cacheInDays").value(0L));
        }

        @Test
        @DisplayName("Deve estourar 404 quando nao encontrar um planeta por id na base de dados.")
        void throw_not_found() throws Exception {
            final String id = UUID.randomUUID().toString();
            mockMvc.perform(get(format("{0}/{1}", Constants.PLANETS_ENDPOINT, id)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(format("Nenhum planeta com id {0} foi encontrado.", id)));

        }

        @Test
        @DisplayName("Deve estourar 404 quando nao encontrar um planeta na API de star wars.")
        void throw_not_found_when_not_found_in_starwars_api() throws Exception {
            MongoPlanet mongoPlanet = saveMongoPlanet(DomainUtils.getRandomMongoPlanet());
            mongoPlanet.setCreationDate(LocalDateTime.now().minus(3, ChronoUnit.DAYS));

            MongoPlanet invalidCacheMongoPlanet = planetMongoRepository.save(mongoPlanet);

            mockMvc.perform(get(format("{0}/{1}", Constants.PLANETS_ENDPOINT, mongoPlanet.getId()))
                            .queryParam("cacheInDays", "0"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(format("Nenhum planeta com nome {0} foi encontrado.", invalidCacheMongoPlanet.getName())));
        }
    }

    @Nested
    class GetByName {

        private static final String ENDPOINT = Constants.PLANETS_ENDPOINT + "/search?name=";

        @Test
        @DisplayName("Deve retornar um planeta buscado na base de dados com sucesso.")
        void find_in_database_successful() throws Exception {
            MongoPlanet mongoPlanet = saveMongoPlanet(DomainUtils.getRandomMongoPlanet());
            mockMvc.perform(get(ENDPOINT + mongoPlanet.getName()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(mongoPlanet.getId()))
                    .andExpect(jsonPath("$.name").value(mongoPlanet.getName()))
                    .andExpect(jsonPath("$.cacheInDays").value(0L))
                    .andExpect(jsonPath("$.movieAppearances").value(mongoPlanet.getMovieAppearances()));
        }

        @Test
        @DisplayName("Deve buscar com sucesso um planeta na API de star wars quando o cache for invalido.")
        void find_in_star_wars_api_successful() throws Exception {
            MongoPlanet mongoPlanet = DomainUtils.getRandomMongoPlanet();
            MongoPlanet newMongoPlanet = MongoPlanet.builder()
                    .id(mongoPlanet.getId())
                    .name("Tatooine")
                    .climate(mongoPlanet.getClimate())
                    .terrain(mongoPlanet.getTerrain())
                    .movieAppearances(mongoPlanet.getMovieAppearances())
                    .creationDate(LocalDateTime.now().minus(2, ChronoUnit.DAYS))
                    .build();

            saveMongoPlanet(newMongoPlanet);

            mockMvc.perform(get(ENDPOINT + newMongoPlanet.getName())
                            .queryParam("cacheInDays", "0"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(newMongoPlanet.getId()))
                    .andExpect(jsonPath("$.name").value(newMongoPlanet.getName()))
                    .andExpect(jsonPath("$.cacheInDays").value(0L));
        }

        @Test
        @DisplayName("Deve estourar 404 quando nao encontrar um planeta por nome na base de dados e na api de star wars.")
        void throw_not_found() throws Exception {
            final String name = "SomeName";
            mockMvc.perform(get(ENDPOINT + name))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(format("Nenhum planeta com nome {0} foi encontrado.", name)));

        }

        @Test
        @DisplayName("Deve estourar 404 quando nao encontrar um planeta na base de dados com cache invalido e nao encontrar na API de star wars.")
        void throw_not_found_when_found_in_database_and_not_in_starwars_api() throws Exception {
            MongoPlanet mongoPlanet = DomainUtils.getRandomMongoPlanet();
            mongoPlanet.setName("SomeName");
            mongoPlanet.setCreationDate(LocalDateTime.now().minus(3, ChronoUnit.DAYS));
            saveMongoPlanet(mongoPlanet);

            mockMvc.perform(get(ENDPOINT + mongoPlanet.getName())
                            .queryParam("cacheInDays", "0"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(format("Nenhum planeta com nome {0} foi encontrado.", mongoPlanet.getName())));
        }
    }

    @Nested
    class UpdateById  {

        private static final String ENDPOINT = Constants.PLANETS_ENDPOINT;

        private static final ObjectMapper mapper = new ObjectMapper();

        @Test
        @DisplayName("Deve atualizar um planeta encontrado na base de dados com sucesso.")
        void update_successful() throws Exception {
            MongoPlanet mongoPlanet = saveMongoPlanet(DomainUtils.getRandomMongoPlanet());
            PlanetJson updatePlanet = DomainUtils.getRandomPlanetJson();
            updatePlanet.setId(mongoPlanet.getId());

            mockMvc.perform(put(ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON.getMediaType())
                            .content(mapper.writeValueAsString(PlanetJson.fromDomain(updatePlanet.toDomain()))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(mongoPlanet.getId()))
                    .andExpect(jsonPath("$.name").value(updatePlanet.getName()))
                    .andExpect(jsonPath("$.climate", hasSize(updatePlanet.getClimate().length)))
                    .andExpect(jsonPath("$.cacheInDays").value(0L));
        }

        @Test
        @DisplayName("Deve estourar 404 quando nao encontrar um planeta na base de dados.")
        void throw_not_found() throws Exception {
            PlanetJson planetJson = DomainUtils.getRandomPlanetJson();
            planetJson.setId(UUID.randomUUID().toString());

            mockMvc.perform(put(ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON.getMediaType())
                            .content(mapper.writeValueAsString(planetJson)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(format("Nenhum planeta com id {0} foi encontrado.", planetJson.getId())));
        }

        @Test
        @DisplayName("Deve estourar 400 quando o planeta tiver campos invalidos.")
        void throw_bad_request() throws Exception {
            PlanetJson planetJson = DomainUtils.getInvalidPlanetJson();
            planetJson.setId(UUID.randomUUID().toString());

            mockMvc.perform(put(ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON.getMediaType())
                            .content(mapper.writeValueAsString(planetJson)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]").value("Campo name nao pode ser nulo."))
                    .andExpect(jsonPath("$.errors[1]").value("Campo climate nao pode ser nulo."))
                    .andExpect(jsonPath("$.errors[2]").value("Campo terrain nao pode ser nulo."))
                    .andExpect(jsonPath("$.errors[3]").value("Campo movieAppearances nao pode ser nulo."));
        }

        @Test
        @DisplayName("Deve estourar 400 quando o planeta tiver id invalido.")
        void throw_bad_request_id() throws Exception {
            PlanetJson planetJson = DomainUtils.getRandomPlanetJson();
            planetJson.setId(null);

            mockMvc.perform(put(ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON.getMediaType())
                            .content(mapper.writeValueAsString(planetJson)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]").value("Campo id nao pode ser nulo."));
        }
    }

    @Nested
    class Save  {


        private static final ObjectMapper mapper = new ObjectMapper();

        @Test
        @DisplayName("Deve salvar um planeta na base de dados com sucesso.")
        void save_successful() throws Exception {
            PlanetJson planetJson = DomainUtils.getRandomPlanetJson();
            planetJson.setId(null);

            mockMvc.perform(MockMvcRequestBuilders.post(Constants.PLANETS_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON.getMediaType())
                            .content(mapper.writeValueAsString(PlanetJson.fromDomain(planetJson.toDomain()))))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", notNullValue()))
                    .andExpect(jsonPath("$.name").value(planetJson.getName()))
                    .andExpect(jsonPath("$.climate", hasSize(planetJson.getClimate().length)))
                    .andExpect(jsonPath("$.cacheInDays").value(0L));
        }

        @Test
        @DisplayName("Deve estourar 400 quando o planeta tiver campos invalidos.")
        void throw_bad_request() throws Exception {
            PlanetJson planetJson = DomainUtils.getInvalidPlanetJson();

            mockMvc.perform(MockMvcRequestBuilders.post(Constants.PLANETS_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON.getMediaType())
                            .content(mapper.writeValueAsString(planetJson)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]").value("Campo name nao pode ser nulo."))
                    .andExpect(jsonPath("$.errors[1]").value("Campo climate nao pode ser nulo."))
                    .andExpect(jsonPath("$.errors[2]").value("Campo terrain nao pode ser nulo."))
                    .andExpect(jsonPath("$.errors[3]").value("Campo movieAppearances nao pode ser nulo."));
        }

    }

    @Nested
    class Delete  {

        @Test
        @DisplayName("Deve deletar um planeta da base de dados pelo id com sucesso.")
        void delete_successful() throws Exception {
            MongoPlanet mongoPlanet = saveMongoPlanet(DomainUtils.getRandomMongoPlanet());

            mockMvc.perform(delete(format("{0}/{1}", Constants.PLANETS_ENDPOINT, mongoPlanet.getId()))
                            .contentType(MediaType.APPLICATION_JSON.getMediaType()))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Deve estourar 404 quando nao encontrar um planeta na base de dados pelo id.")
        void throw_not_found() throws Exception {
            final String id = "some-id";

            mockMvc.perform(delete(format("{0}/{1}", Constants.PLANETS_ENDPOINT, id))
                            .contentType(MediaType.APPLICATION_JSON.getMediaType()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(format("Nenhum planeta foi encontrado para ser deletado pelo id: {0}.", id)));
        }

    }
}
