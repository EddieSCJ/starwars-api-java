package com.api.starwars.planets.storage;

import com.api.starwars.commons.exceptions.http.HttpNotFoundException;
import com.api.starwars.planets.model.domain.Planet;
import com.api.starwars.planets.model.mongo.MongoPlanet;
import com.api.starwars.planets.storage.interfaces.ISNSManager;
import com.api.starwars.planets.storage.interfaces.ISQSManager;
import com.mongodb.client.result.DeleteResult;
import commons.utils.DomainUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.StringUtils;

import java.util.Optional;

import static com.api.starwars.planets.model.mongo.Constants.FIELD_ID;
import static com.api.starwars.planets.model.mongo.Constants.FIELD_NAME;
import static commons.utils.DomainUtils.FAKE_ID;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class PlanetRepositoryTest {
    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private ISQSManager sqsManager;

    @Mock
    private ISNSManager snsManager;

    @InjectMocks
    private PlanetRepository planetRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Criteria getFindByNameCriteria(String FAKE_NAME) {
        Criteria lowercase = where(FIELD_NAME).is(FAKE_NAME.toLowerCase());
        Criteria uppercase = where(FIELD_NAME).is(FAKE_NAME.toUpperCase());
        Criteria capitalized = where(FIELD_NAME).is(StringUtils.capitalize(FAKE_NAME));

        return new Criteria().orOperator(lowercase, uppercase, capitalized);
    }

    @Nested
    class Count {
        @Test
        @DisplayName("Deve retornar a quantidade de documentos na colecao")
        void count() {
            Criteria criteria = where(FIELD_ID).exists(true);

            planetRepository.count();
            verify(mongoTemplate, times(1)).count(eq(query(criteria)), eq(MongoPlanet.class));
        }

    }

    @Nested
    class FindById {
        @Test
        @DisplayName("Deve retornar um Optional nao vazio de MongoPlanet da database.")
        void successful() {
            when(mongoTemplate.findById(eq(FAKE_ID), eq(MongoPlanet.class))).thenReturn(DomainUtils.getRandomMongoPlanet());

            planetRepository.findById(FAKE_ID);
            verify(mongoTemplate, times(1)).findById(eq(FAKE_ID), eq(MongoPlanet.class));
        }

        @Test
        @DisplayName("Deve estourar 404 quando nao encontrar nenhum planeta com o id passado na database.")
        void when_not_found() {
            Criteria criteria = where(FIELD_ID).is(FAKE_ID);
            when(mongoTemplate.findById(FAKE_ID, eq(any()))).thenReturn(Optional.empty());

            assertThrows(HttpNotFoundException.class, () -> planetRepository.findById(FAKE_ID));
            verify(mongoTemplate, times(1)).findById(eq(FAKE_ID), eq(MongoPlanet.class));

        }

    }

    @Nested
    class FindByName {
        @Test
        @DisplayName("Deve retornar um Optional nao vazio")
        void successful() {
            final String FAKE_NAME = "fake_name";
            Criteria criteria = getFindByNameCriteria(FAKE_NAME);
            when(mongoTemplate.findOne(query(criteria), MongoPlanet.class)).thenReturn(DomainUtils.getRandomMongoPlanet());

            planetRepository.findByName(FAKE_NAME);
            verify(mongoTemplate, times(1)).findOne(eq(query(criteria)), eq(MongoPlanet.class));
        }

        @Test
        @DisplayName("Deve estourar 404 quando nao encontrar nenhum planeta na database com o nome passado.")
        void fail_when_not_found() {
            final String FAKE_NAME = "fake_name";
            Criteria criteria = getFindByNameCriteria(FAKE_NAME);

            when(mongoTemplate.findOne(query(criteria), eq(any()))).thenReturn(Optional.empty());

            assertThrows(HttpNotFoundException.class, () -> planetRepository.findByName(FAKE_NAME));
            verify(mongoTemplate, times(1)).findOne(eq(query(criteria)), eq(MongoPlanet.class));
        }

    }

    @Nested
    class Save {
        @Test
        @DisplayName("Deve salvar com sucesso um MongoPlanet")
        void successful() {
            Planet planet = DomainUtils.getRandomPlanet();
            MongoPlanet mongoPlanet = MongoPlanet.fromDomain(planet);

            when(mongoTemplate.save(any())).thenReturn(mongoPlanet);
            planetRepository.save(planet);

            verify(mongoTemplate, times(1)).save(any());
        }
    }

    @Nested
    class DeleteById {

        @Test
        @DisplayName("Deve buscar por id, encontrar e deletar com sucesso o MongoPlanet.")
        void successful() {
            Criteria criteria = where(FIELD_ID).is(FAKE_ID);

            when(mongoTemplate.findById(FAKE_ID, MongoPlanet.class)).thenReturn(DomainUtils.getRandomMongoPlanet());
            when(mongoTemplate.remove(query(criteria), MongoPlanet.class)).thenReturn(DeleteResult.acknowledged(1L));

            planetRepository.deleteById(FAKE_ID);
            verify(mongoTemplate, times(1)).remove(query(criteria), MongoPlanet.class);
            verify(sqsManager, times(1)).sendDeleteMessage(anyString());
            verify(snsManager, times(1)).sendDeleteNotification(anyString());
        }

        @Test
        @DisplayName("Deve estourar um 404 ao tentar deletar pois nao existe documento com o id passado.")
        void fail_when_not_found() {
            Criteria criteria = where(FIELD_ID).is(FAKE_ID);
            when(mongoTemplate.remove(query(criteria), MongoPlanet.class)).thenReturn(DeleteResult.acknowledged(0L));

            assertThrows(HttpNotFoundException.class, () -> planetRepository.deleteById(FAKE_ID));
        }
    }

}
