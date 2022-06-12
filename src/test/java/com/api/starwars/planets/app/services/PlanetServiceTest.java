//package com.api.starwars.planets.app.services;
//
//import com.api.starwars.common.exceptions.http.BadRequestError;
//import com.api.starwars.common.exceptions.http.NotFoundError;
//import com.api.starwars.planets.app.storage.mongo.IPlanetMongoRepository;
//import com.api.starwars.planets.app.storage.mongo.model.MongoPlanet;
//import com.api.starwars.planets.app.validations.PlanetValidator;
//import com.api.starwars.planets.domain.client.StarWarsApiClient;
//import com.api.starwars.planets.domain.model.Planet;
//import com.api.starwars.planets.domain.model.client.PlanetResponseJson;
//import com.api.starwars.planets.domain.storage.PlanetStorage;
//import commons.utils.DomainUtils;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
//
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.util.Collections;
//import java.util.List;
//import java.util.Objects;
//import java.util.Optional;
//
//import static commons.utils.DomainUtils.FAKE_ID;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//class PlanetServiceTest {
//
//    @Mock
//    IPlanetMongoRepository planetMongoRepository;
//
//    @Mock
//    PlanetStorage planetRepository;
//
//    @Mock
//    StarWarsApiClient starWarsApiClient;
//
//    @Mock
//    PlanetValidator planetValidator;
//
//    @InjectMocks
//    PlanetService planetService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Nested
//    class FindAll {
//
//        @Test
//        @DisplayName("Deve retornar uma lista nao vazia quando execetuar um findAll na base de dados.")
//        void successfully_from_database() {
//            Page<MongoPlanet> originalPage = new PageImpl<>(DomainUtils.getRandomMongoPlanetList());
//            when(planetRepository.count()).thenReturn(3L);
//            when(planetMongoRepository.findAll(any(PageRequest.class))).thenReturn(originalPage);
//
//            Page<Planet> returnedPage = planetService.findAll(1, "name", Sort.Direction.ASC.name(), 15);
//            assertEquals(originalPage.getTotalElements(), returnedPage.getTotalElements());
//            assertEquals(originalPage.getTotalPages(), returnedPage.getTotalPages());
//            assertEquals(originalPage.getSort(), returnedPage.getSort());
//            assertEquals(originalPage.getNumber(), returnedPage.getNumber());
//            assertEquals(originalPage.getNumberOfElements(), returnedPage.getNumberOfElements());
//        }
//
//        @Test
//        @DisplayName("Deve retornar uma lista vazia da database e executar com sucesso um find na api do star wars.")
//        void successfully_from_star_wars_api_client() {
//            PlanetResponseJson planetResponseJson = DomainUtils.getRandomPlanetResponseJson();
//            List<MongoPlanet> mongoPlanets = planetResponseJson.getResults().stream()
//                    .map(mPlanetJson -> mPlanetJson.toDomain(null))
//                    .map(MongoPlanet::fromDomain)
//                    .toList();
//
//            when(planetRepository.count()).thenReturn(0L);
//            when(starWarsApiClient.getPlanets()).thenReturn(planetResponseJson);
//            when(planetMongoRepository.saveAll(anyList())).thenReturn(mongoPlanets);
//
//            Page<Planet> returnedPage = planetService.findAll(0, "name", Sort.Direction.ASC.name(), 15);
//
//            assertEquals(1, returnedPage.getTotalPages());
//            assertEquals(0, returnedPage.getNumber());
//            assertEquals(1, returnedPage.getNumberOfElements());
//            assertEquals(1, returnedPage.getTotalElements());
//            assertEquals(Sort.by("name").ascending(), returnedPage.getSort());
//        }
//
//        @Test
//        @DisplayName("Deve estourar 404 quando nao encontrar nada na api de star wars e a base de dados estiver vazia.")
//        void fail_when_not_found_from_star_wars_api_client() {
//            PlanetResponseJson planetResponseJson = DomainUtils.getEmptyPlanetResponseJson();
//
//            when(planetRepository.count()).thenReturn(0L);
//            when(starWarsApiClient.getPlanets()).thenReturn(planetResponseJson);
//
//            assertThrows(NotFoundError.class, () -> planetService.findAll(0, "name", Sort.Direction.ASC.name(), 15));
//        }
//
//    }
//
//    @Nested
//    class FindById {
//        @Test
//        @DisplayName("Deve retornar planeta com sucesso.")
//        void successful() {
//            Planet storedPlanet = DomainUtils.getRandomPlanet();
//            when(planetRepository.findById(FAKE_ID)).thenReturn(Optional.of(storedPlanet));
//
//            Planet planet = planetService.findById(FAKE_ID, 0L);
//
//            assertTrue(Objects.deepEquals(storedPlanet, planet));
//        }
//
//        @Test
//        @DisplayName("Deve estourar 404 quando nao encontrar nenhum planeta na base de dados.")
//        void fail_when_not_found() {
//            when(planetRepository.findById(FAKE_ID)).thenReturn(Optional.empty());
//            assertThrows(NotFoundError.class, () -> planetService.findById(FAKE_ID, 0L));
//        }
//
//        @Test
//        @DisplayName("Deve buscar e retornar um planeta da api de star wars quando o cache do planeta encontrado na base for invalido.")
//        void successful_from_star_wars_api_client() throws IOException, InterruptedException {
//            MongoPlanet mongoPlanet = DomainUtils.getRandomMongoPlanet();
//            mongoPlanet.setCreationDate(LocalDateTime.of(2001, 12, 12, 12, 12));
//
//            PlanetResponseJson planetResponseJson = DomainUtils.getRandomPlanetResponseJson();
//            planetResponseJson.getResults().get(0).setName(mongoPlanet.getName());
//
//            Planet apiClientPlanet = planetResponseJson.getResults().get(0).toDomain(mongoPlanet.getId().toString());
//            MongoPlanet savedPlanet = MongoPlanet.fromDomain(apiClientPlanet);
//
//            when(planetRepository.findById(FAKE_ID)).thenReturn(Optional.of(mongoPlanet.toDomain()));
//            when(planetRepository.save(any(Planet.class))).thenReturn(savedPlanet.toDomain());
//            when(starWarsApiClient.getPlanetBy(mongoPlanet.getName())).thenReturn(planetResponseJson);
//
//            Planet planet = planetService.findById(FAKE_ID, 0L);
//            assertTrue(Objects.deepEquals(savedPlanet.toDomain(), planet));
//        }
//
//        @Test
//        @DisplayName("Deve buscar e estourar 404 quando nao encontrar na api de star wars e o cache do planeta na base for invalido.")
//        void fail_when_not_found_from_star_wars_api() throws IOException, InterruptedException {
//            MongoPlanet mongoPlanet = DomainUtils.getRandomMongoPlanet();
//            mongoPlanet.setCreationDate(LocalDateTime.of(2001, 12, 12, 12, 12));
//
//            PlanetResponseJson planetResponseJson = DomainUtils.getEmptyPlanetResponseJson();
//
//            when(planetRepository.findById(FAKE_ID)).thenReturn(Optional.of(mongoPlanet.toDomain()));
//            when(starWarsApiClient.getPlanetBy(mongoPlanet.getName())).thenReturn(planetResponseJson);
//
//            assertThrows(NotFoundError.class, () -> planetService.findById(FAKE_ID, 0L));
//        }
//    }
//
//    @Nested
//    class FindByName {
//        @Test
//        @DisplayName("Deve retornar planeta com sucesso.")
//        void successful() {
//            Planet mongoPlanet = DomainUtils.getRandomPlanet();
//
//            when(planetRepository.findByName(mongoPlanet.name())).thenReturn(Optional.of(mongoPlanet));
//
//            Planet planet = planetService.findByName(mongoPlanet.name(), 0L);
//
//            assertTrue(Objects.deepEquals(mongoPlanet, planet));
//        }
//
//        @Test
//        @DisplayName("Deve retornar planeta com sucesso quando receber um Optional vazio da database e um resultado nao vazio da api de star wars.")
//        void successful_from_star_wars_api() throws IOException, InterruptedException {
//            PlanetResponseJson planetResponseJson = DomainUtils.getRandomPlanetResponseJson();
//            MongoPlanet mongoPlanet = MongoPlanet.fromDomain(planetResponseJson.getResults().get(0).toDomain(FAKE_ID));
//            mongoPlanet.setCreationDate(LocalDateTime.of(2021, 12, 12, 12, 12));
//
//            when(planetRepository.findByName(mongoPlanet.getName())).thenReturn(Optional.of(mongoPlanet.toDomain()));
//            when(starWarsApiClient.getPlanetBy(mongoPlanet.getName())).thenReturn(planetResponseJson);
//            when(planetRepository.save(any(Planet.class))).thenReturn(mongoPlanet.toDomain());
//
//            Planet planet = planetService.findByName(mongoPlanet.getName(), 0L);
//
//            assertTrue(Objects.deepEquals(mongoPlanet.toDomain(), planet));
//        }
//
//        @Test
//        @DisplayName("Deve estourar 404 quando retornar um optional vazio da database e uma lista vazia da api de star wars")
//        void fail_when_not_found() throws IOException, InterruptedException {
//            final String FAKE_NAME = "fake_name";
//            PlanetResponseJson planetResponseJson = DomainUtils.getEmptyPlanetResponseJson();
//
//            when(planetRepository.findByName(FAKE_NAME)).thenReturn(Optional.empty());
//            when(starWarsApiClient.getPlanetBy(FAKE_NAME)).thenReturn(planetResponseJson);
//
//            assertThrows(NotFoundError.class, () -> planetService.findByName(FAKE_NAME, 0L));
//        }
//    }
//
//    @Nested
//    class UpdateById {
//
//        @Test
//        @DisplayName("Deve atualizar planeta por id com sucesso.")
//        void successful() {
//            Planet oldPlanet = DomainUtils.getRandomPlanet();
//            Planet newPlanet = DomainUtils.getRandomPlanet();
//
//            when(planetValidator.validate(any(Planet.class))).thenReturn(Collections.emptyList());
//            when(planetRepository.findById(newPlanet.id())).thenReturn(Optional.of(oldPlanet));
//            when(planetRepository.save(any(Planet.class))).thenReturn(newPlanet);
//
//            Planet planet = planetService.updateById(newPlanet.id(), newPlanet);
//
//            assertTrue(Objects.deepEquals(newPlanet, planet));
//        }
//
//        @Test
//        @DisplayName("Deve estourar 400 ao validar planeta")
//        void fail_when_invalid_fields() {
//            Planet invalidPlanet = DomainUtils.getInvalidPlanet();
//
//            when(planetValidator.validate(any(Planet.class))).thenReturn(List.of("Campo X nao pode ser nulo."));
//
//            assertThrows(BadRequestError.class, () -> planetService.updateById(FAKE_ID, invalidPlanet));
//        }
//
//        @Test
//        @DisplayName("Deve estourar 404 quando nao encontrar planeta para ser atualizado por id.")
//        void fail_when_not_found() {
//            when(planetValidator.validate(any(Planet.class))).thenReturn(Collections.emptyList());
//            when(planetRepository.findById(FAKE_ID)).thenReturn(Optional.empty());
//
//            assertThrows(NotFoundError.class, () -> planetService.updateById(FAKE_ID, DomainUtils.getRandomPlanet()));
//        }
//    }
//
//    @Nested
//    class Save {
//
//        @Test
//        @DisplayName("Deve salvar planeta com sucesso")
//        void successful() {
//            Planet storedPlanet = DomainUtils.getRandomPlanet();
//            when(planetValidator.validate(any(Planet.class))).thenReturn(Collections.emptyList());
//            when(planetRepository.save(any(Planet.class))).thenReturn(storedPlanet);
//
//            Planet planet = planetService.save(storedPlanet);
//            assertTrue(Objects.deepEquals(storedPlanet, planet));
//        }
//
//        @Test
//        @DisplayName("Deve estourar 400 ao validar planeta.")
//        void fail_when_invalid_fields() {
//            when(planetValidator.validate(any(Planet.class))).thenReturn(List.of("Campo X nao deve ser vazio"));
//
//            assertThrows(BadRequestError.class, () -> planetService.save(DomainUtils.getInvalidPlanet()));
//        }
//
//        @Test
//        @DisplayName("Deve salvar todos os planetas enviados com sucesso.")
//        void successful_save_all() {
//            List<MongoPlanet> mongoPlanets = DomainUtils.getRandomMongoPlanetList();
//            List<Planet> planets = mongoPlanets.parallelStream().map(MongoPlanet::toDomain).toList();
//
//            when(planetMongoRepository.saveAll(anyList())).thenReturn(mongoPlanets);
//
//            List<Planet> savedPlanets = planetService.saveAll(planets);
//            assertTrue(Objects.deepEquals(planets, savedPlanets));
//        }
//    }
//
//    @Nested
//    class Delete {
//
//        @Test
//        @DisplayName("Deve deletar com sucesso por id.")
//        void successful() {
//            assertDoesNotThrow(() -> planetService.deleteById(FAKE_ID));
//            verify(planetRepository, times(1)).deleteById(anyString());
//        }
//
//        @Test
//        @DisplayName("Deve replicar excecao do metodo delete do repository.")
//        void fail() {
//            doThrow(NotFoundError.class).when(planetRepository).deleteById(anyString());
//
//            assertThrows(NotFoundError.class, () -> planetService.deleteById(FAKE_ID));
//            verify(planetRepository, times(1)).deleteById(anyString());
//        }
//
//    }
//
//}
//
//
