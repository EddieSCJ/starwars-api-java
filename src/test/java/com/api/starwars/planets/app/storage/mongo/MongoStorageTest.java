package com.api.starwars.planets.app.storage.mongo;

import com.api.starwars.common.exceptions.http.NotFoundError;
import com.api.starwars.planets.app.storage.mongo.model.MongoPlanet;
import com.api.starwars.planets.domain.message.MessageSender;
import com.api.starwars.planets.domain.message.NotificationSender;
import com.api.starwars.planets.domain.model.Planet;
import com.mongodb.client.result.DeleteResult;
import commons.utils.DomainUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.api.starwars.planets.app.storage.mongo.model.Constants.FIELD_ID;
import static com.api.starwars.planets.app.storage.mongo.model.Constants.FIELD_NAME;
import static commons.utils.DomainUtils.FAKE_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class MongoStorageTest {
    @Mock
    private ReactiveMongoTemplate mongoTemplate;

    @Mock
    @Qualifier("SQSManager")
    private MessageSender messageSender;

    @Mock
    private NotificationSender snsManager;

    @InjectMocks
    private MongoStorage planetRepository;

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
            when(mongoTemplate.estimatedCount(MongoPlanet.class)).thenReturn(Mono.just(0L));

            StepVerifier.create(planetRepository.count())
                    .expectNext(0L)
                    .expectComplete()
                    .verify();

            verify(mongoTemplate, times(1)).estimatedCount(eq(MongoPlanet.class));
        }

    }

    @Nested
    class FindById {
        @Test
        @DisplayName("Deve retornar um Mono nao vazio de MongoPlanet da database.")
        void successful() {
            MongoPlanet queryResult = DomainUtils.getRandomMongoPlanet();
            Mono<MongoPlanet> mongoPlanet = Mono.just(queryResult);

            Planet planet = queryResult.toDomain();

            when(mongoTemplate.findById(eq(FAKE_ID), eq(MongoPlanet.class))).thenReturn(mongoPlanet);

            StepVerifier
                    .create(planetRepository.findById(FAKE_ID))
                    .expectNext(planet)
                    .expectComplete()
                    .verify();

            verify(mongoTemplate, times(1)).findById(eq(FAKE_ID), eq(MongoPlanet.class));
        }

        @Test
        @DisplayName("Deve estourar 404 quando nao encontrar nenhum planeta com o id passado na database.")
        void when_not_found() {
            when(mongoTemplate.findById(FAKE_ID, MongoPlanet.class)).thenReturn(Mono.empty());

            StepVerifier
                    .create(planetRepository.findById(FAKE_ID))
                    .expectComplete()
                    .verify();
            verify(mongoTemplate, times(1)).findById(eq(FAKE_ID), eq(MongoPlanet.class));
        }

    }

    @Nested
    class FindByName {
        @Test
        @DisplayName("Deve retornar um Mono nao vazio")
        void successful() {
            final String FAKE_NAME = "fake_name";
            MongoPlanet mongoPlanet = DomainUtils.getRandomMongoPlanet();
            mongoPlanet.setName(FAKE_NAME);

            Criteria criteria = getFindByNameCriteria(FAKE_NAME);
            when(mongoTemplate.findOne(query(criteria), MongoPlanet.class)).thenReturn(Mono.just(mongoPlanet));

            StepVerifier
                    .create(planetRepository.findByName(FAKE_NAME))
                    .expectNext(mongoPlanet.toDomain())
                    .expectComplete()
                    .verify();
            verify(mongoTemplate, times(1)).findOne(eq(query(criteria)), eq(MongoPlanet.class));
        }

        @Test
        @DisplayName("Deve estourar 404 quando nao encontrar nenhum planeta na database com o nome passado.")
        void fail_when_not_found() {
            final String FAKE_NAME = "fake_name";

            Criteria criteria = getFindByNameCriteria(FAKE_NAME);
            when(mongoTemplate.findOne(query(criteria), MongoPlanet.class)).thenReturn(Mono.empty());

            StepVerifier
                    .create(planetRepository.findByName(FAKE_NAME))
                    .expectComplete()
                    .verify();
            verify(mongoTemplate, times(1)).findOne(eq(query(criteria)), eq(MongoPlanet.class));
        }

    }

    @Nested
    class Save {
        @Test
        @DisplayName("Deve salvar com sucesso um MongoPlanet")
        void successful() {
            Planet planet = DomainUtils.getRandomPlanet();
            Mono<Planet> planetMono = Mono.just(planet);
            Mono<MongoPlanet> mongoPlanetMono = planetMono.as(mono -> mono.map(MongoPlanet::fromDomain));

            when(mongoTemplate.save(any())).thenReturn(Mono.create(sink -> sink.success(mongoPlanetMono)));

            StepVerifier
                    .create(planetRepository.save(planetMono))
                    .expectNext(planet)
                    .expectComplete()
                    .verify();
            verify(mongoTemplate, times(1)).save(any(MongoPlanet.class));
        }
    }

    @Nested
    class Delete {

        @Test
        @DisplayName("Deve buscar por id, encontrar e deletar com sucesso o MongoPlanet.")
        void successful() {
            Criteria criteria = where(FIELD_ID).is(FAKE_ID);

            when(mongoTemplate.findById(FAKE_ID, MongoPlanet.class)).thenReturn(Mono.just(DomainUtils.getRandomMongoPlanet()));
            when(mongoTemplate.remove(query(criteria), MongoPlanet.class)).thenReturn(Mono.just(DeleteResult.acknowledged(1L)));

            StepVerifier
                    .create(planetRepository.deleteById(FAKE_ID))
                    .expectNextCount(1)
                    .expectComplete()
                    .verify();

            verify(mongoTemplate, times(1)).remove(query(criteria), MongoPlanet.class);
//            verify(messageSender, times(2)).sendMessage(anyString());
//            verify(snsManager, times(1)).sendNotification(anyString(), anyString());
        }

        @Test
        @DisplayName("Deve estourar um 404 ao tentar deletar pois nao existe documento com o id passado.")
        void fail_when_not_found() {
            Criteria criteria = where(FIELD_ID).is(FAKE_ID);
            when(mongoTemplate.findById(FAKE_ID, MongoPlanet.class)).thenReturn(Mono.empty());
            when(mongoTemplate.remove(query(criteria), MongoPlanet.class)).thenReturn(Mono.just(DeleteResult.acknowledged(0L)));

            StepVerifier
                    .create(planetRepository.deleteById(FAKE_ID))
                    .expectError(NotFoundError.class)
                    .verify();

            verify(mongoTemplate, times(1)).remove(query(criteria), MongoPlanet.class);
        }
    }

}
