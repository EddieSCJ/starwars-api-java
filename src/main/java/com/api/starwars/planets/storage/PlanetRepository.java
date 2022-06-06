package com.api.starwars.planets.storage;

import com.api.starwars.common.exceptions.http.HttpInternalServerErrorException;
import com.api.starwars.common.exceptions.http.HttpNotFoundException;
import com.api.starwars.planets.model.domain.Event;
import com.api.starwars.planets.model.domain.Planet;
import com.api.starwars.planets.model.mongo.MongoPlanet;
import com.api.starwars.planets.model.view.EventJson;
import com.api.starwars.planets.services.interfaces.IPlanetRepository;
import com.api.starwars.planets.storage.interfaces.IMessageSender;
import com.api.starwars.planets.storage.interfaces.INotificationSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.client.result.DeleteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

import static com.api.starwars.planets.model.mongo.Constants.FIELD_ID;
import static com.api.starwars.planets.model.mongo.Constants.FIELD_NAME;
import static java.text.MessageFormat.format;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Slf4j
@Repository
public class PlanetRepository implements IPlanetRepository {

    @Qualifier("mongoTemplate")
    private final MongoTemplate mongoTemplate;
    private final IMessageSender sqsManager;
    private final IMessageSender kafkaManager;
    private final INotificationSender snsManager;

    @Autowired
    public PlanetRepository(MongoTemplate mongoTemplate,
                            @Qualifier("SQSManager") IMessageSender sqsManager,
                            IMessageSender kafkaManager,
                            INotificationSender snsManager) {
        this.mongoTemplate = mongoTemplate;
        this.sqsManager = sqsManager;
        this.kafkaManager = kafkaManager;
        this.snsManager = snsManager;
    }

    public Long count() {
        log.info("Iniciando contagem de planetas no banco.");
        Criteria criteria = where(FIELD_ID).exists(true);
        Long count = mongoTemplate.count(query(criteria), MongoPlanet.class);

        log.info("Contagem de planetas no banco concluida com sucesso. planetas: {}.", count);
        return count;
    }

    @Override
    public Optional<Planet> findById(String id) {
        log.info("Iniciando busca de planeta no banco pelo id. id: {}.", id);
        Criteria criteria = where(FIELD_ID).is(id);
        Optional<MongoPlanet> planet = Optional.ofNullable(mongoTemplate.findById(id, MongoPlanet.class));

        if (planet.isEmpty()) {
            log.info("Busca de planeta no banco pelo id concluída com sucesso. id: {}. Planeta nao encontrado.", id);
            throw new HttpNotFoundException(format("Nenhum planeta com id {0} foi encontrado.", id));
        }

        log.info("Busca de planeta no banco pelo id concluida com sucesso. id: {}.", id);
        return planet.map(MongoPlanet::toDomain);
    }

    @Override
    public Optional<Planet> findByName(String name) {
        log.info("Iniciando busca de planeta no banco pelo nome. name: {}.", name);
        Criteria lowercase = where(FIELD_NAME).is(name.toLowerCase());
        Criteria uppercase = where(FIELD_NAME).is(name.toUpperCase());
        Criteria capitalized = where(FIELD_NAME).is(StringUtils.capitalize(name));

        Criteria criteria = new Criteria().orOperator(lowercase, uppercase, capitalized);
        Optional<MongoPlanet> planet = Optional.ofNullable(mongoTemplate.findOne(query(criteria), MongoPlanet.class));

        if (planet.isEmpty()) {
            log.info("Busca de planeta no banco pelo nome concluida com sucesso. name: {}. Planeta nao encontrado.", name);
            throw new HttpNotFoundException(format("Nenhum planeta com nome {0} foi encontrado.", name));
        }

        log.info("Busca de planeta no banco pelo nome concluida com sucesso. name: {}.", name);
        return planet.map(MongoPlanet::toDomain);
    }

    @Override
    public Planet save(Planet planet) {
        log.info("Iniciando criacao planeta no banco. name: {}.", planet.name());
        MongoPlanet mongoPlanet = MongoPlanet.fromDomain(planet);
        MongoPlanet savedMongoPlanet = mongoTemplate.save(mongoPlanet);

        log.info("Criacao planeta no banco concluida com sucesso. id: {}. name: {}.", planet.id(), planet.name());
        return savedMongoPlanet.toDomain();
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteById(String id) {
        log.info("Iniciando exclusao de planeta no banco pelo id. id: {}.", id);
        Optional<MongoPlanet> mongoPlanet = Optional.ofNullable(mongoTemplate.findById(id, MongoPlanet.class));
        if (mongoPlanet.isEmpty()) {
            log.info("Nenhum planeta foi deletado por id. id: {}.", id);
            throw new HttpNotFoundException(format("Nenhum planeta foi encontrado para ser deletado pelo id: {0}.", id));
        }

        Criteria criteria = where(FIELD_ID).is(id);
        DeleteResult deleteResult = mongoTemplate.remove(query(criteria), MongoPlanet.class);
        if (deleteResult.getDeletedCount() == 0) {
            log.info("Nenhum planeta foi deletado por id. id: {}.", id);
            throw new HttpInternalServerErrorException(format("Nenhum planeta deletado pelo id: {0}.", id));
        }

        this.sendEvents(mongoPlanet.get());
        log.info("Exclusao de planeta no banco pelo id concluida com sucesso. id: {}.", id);
    }

    private void sendEvents(MongoPlanet mongoPlanet) {
        Event event = new Event("planet", "delete", mongoPlanet.getName());
        String json;

        try {
            json = EventJson.fromDomain(event).toJson();
        } catch (JsonProcessingException exception) {
            throw new HttpInternalServerErrorException(exception.getMessage());
        }

        sqsManager.sendMessage(json);
        kafkaManager.sendMessage(json);
        snsManager.sendNotification("Planet deletion", json);
    }
}
