package com.api.starwars.planets.app.storage.mongo;

import com.api.starwars.common.exceptions.http.InternalServerError;
import com.api.starwars.planets.app.storage.mongo.model.MongoPlanet;
import com.api.starwars.planets.domain.model.Planet;
import com.api.starwars.planets.domain.model.event.Event;
import com.api.starwars.planets.domain.model.event.EventEnum;
import com.api.starwars.planets.domain.model.view.EventView;
import com.api.starwars.planets.domain.storage.PlanetStorage;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import static com.api.starwars.planets.app.storage.mongo.model.Constants.FIELD_ID;
import static com.api.starwars.planets.app.storage.mongo.model.Constants.FIELD_NAME;
import static java.text.MessageFormat.format;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Slf4j
@Repository
public class MongoStorage implements PlanetStorage {

    @Qualifier("mongoTemplate")
    private final ReactiveMongoTemplate mongoTemplate;
//    private final MessageSender sqsManager;
//    private final MessageSender kafkaManager;
//    private final NotificationSender snsManager;

    @Autowired
    public MongoStorage(ReactiveMongoTemplate mongoTemplate
//                        @Qualifier("SQSManager") MessageSender sqsManager,
//                        MessageSender kafkaManager,
//                        NotificationSender snsManager
    ) {
        this.mongoTemplate = mongoTemplate;
//        this.sqsManager = sqsManager;
//        this.kafkaManager = kafkaManager;
//        this.snsManager = snsManager;
    }

    public Mono<Long> count() {
        log.info("Iniciando contagem de planetas no banco.");
        Mono<Long> count = mongoTemplate
                .estimatedCount(MongoPlanet.class)
                .doOnNext(counter -> log.info("Contagem de planetas no banco concluida com sucesso. planetas: {}.", counter));

        return count;
    }

    @Override
    public Mono<Planet> findById(String id) {
        log.info("Iniciando busca de planeta no banco pelo id. id: {}.", id);

        Mono<Planet> planet = mongoTemplate
                .findById(id, MongoPlanet.class)
                .doOnSuccess(p -> log.info("Planeta encontrado no banco. id: {}.", id))
                .flatMap(mongoPlanet -> Mono.just(mongoPlanet.toDomain()))
                .doOnSuccess(p -> log.info("Busca de planeta no banco pelo id concluida com sucesso. id: {}.", id));
        return planet;
    }

    @Override
    public Mono<Planet> findByName(String name) {
        log.info("Iniciando busca de planeta no banco pelo nome. name: {}.", name);
        Criteria lowercase = where(FIELD_NAME).is(name.toLowerCase());
        Criteria uppercase = where(FIELD_NAME).is(name.toUpperCase());
        Criteria capitalized = where(FIELD_NAME).is(StringUtils.capitalize(name));
        Criteria criteria = new Criteria().orOperator(lowercase, uppercase, capitalized);

        Mono<Planet> planet = mongoTemplate
                .findOne(query(criteria), MongoPlanet.class)
                .doOnSuccess(p -> log.info("Planeta encontrado no banco. name: {}.", name))
                .flatMap(mongoPlanet -> Mono.just(mongoPlanet.toDomain()))
                .doOnSuccess(p -> log.info("Busca de planeta no banco pelo nome concluida com sucesso. name: {}.", name));

        return planet;
    }

    @Override
    public Mono<Planet> save(Mono<Planet> planet) {
        Mono<MongoPlanet> mongoPlanet = planet.as(planetMono -> planetMono.map(MongoPlanet::fromDomain));
        return mongoTemplate
                .save(mongoPlanet)
                .map(MongoPlanet::toDomain)
                .doOnSuccess(p -> log.info("Planeta salvo no banco. id: {}.", p.id()));
    }

    @Transactional(rollbackFor = Exception.class)
    public Mono<Planet> deleteById(String id) {
        log.info("Iniciando exclusao de planeta no banco pelo id. id: {}.", id);
        Criteria criteria = where(FIELD_ID).is(id);

        return this.findById(id)
                .switchIfEmpty(Mono.error(new InternalServerError(format("Planeta não encontrado para ser deletado. id: {0}.", id))))
                .zipWhen(p -> mongoTemplate.remove(query(criteria), MongoPlanet.class))
                .doOnSuccess(tuple -> {
                    if(tuple.getT2().getDeletedCount() == 0) {
                        log.error("Nenhum planeta foi excluido. id: {}.", id);
                        throw new InternalServerError(format("Nenhum planeta foi excluido. id: {0}.", id));
                    }

                    this.sendEvents(tuple.getT1(), EventEnum.DELETE);
                })
                .map(Tuple2::getT1);
    }

    private void sendEvents(Planet planet, EventEnum eventEnum) {
        Event event = new Event("planet", eventEnum, planet.name());
        String json;
        log.info("sent event ahahaahaaahaha 11213");
        try {
            json = EventView.fromDomain(event).toJson();
        } catch (JsonProcessingException exception) {
            throw new InternalServerError(exception.getMessage());
        }

//        sqsManager.sendMessage(json);
//        kafkaManager.sendMessage(json);
//        snsManager.sendNotification("Planet deletion", json);
    }
}