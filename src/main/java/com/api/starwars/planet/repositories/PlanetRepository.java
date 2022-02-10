package com.api.starwars.planet.repositories;

import com.api.starwars.planet.model.domain.Planet;
import com.api.starwars.planet.model.mongo.MongoPlanet;
import com.mongodb.client.result.DeleteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Optional;

import static com.api.starwars.planet.model.mongo.Constants.FIELD_ID;
import static com.api.starwars.planet.model.mongo.Constants.FIELD_NAME;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Slf4j
@Repository
public class PlanetRepository implements IPlanetRepository {

    @Qualifier("mongoTemplate")
    private final MongoTemplate mongoTemplate;

    @Autowired
    public PlanetRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Long count() {
        log.info("Iniciando contagem de planetas no banco.");
        Criteria criteria = where(FIELD_ID).exists(true);
        Long count = mongoTemplate.count(query(criteria), MongoPlanet.class);

        log.info("Contagem de planetas no banco concluída com sucesso. planetas: {}.", count);
        return count;
    }

    @Override
    public Optional<MongoPlanet> findbyId(String id) {
        log.info("Iniciando busca de planeta no banco pelo id. id: {}.", id);
        Criteria criteria = where(FIELD_ID).is(id);
        Optional<MongoPlanet> planet = Optional.ofNullable(mongoTemplate.findOne(query(criteria), MongoPlanet.class));
        if (planet.isEmpty()) {
            log.warn("Busca de planeta no banco pelo id concluída com sucesso. id: {}. Planeta não encontrado.", id);
        }
        log.info("Busca de planeta no banco pelo id concluída com sucesso. id: {}.", id);
        return planet;
    }

    @Override
    public Optional<MongoPlanet> findByName(String name) {
        log.info("Iniciando busca de planeta no banco pelo nome. name: {}.", name);
        Criteria lowercase = where(FIELD_NAME).is(name.toLowerCase());
        Criteria uppercase = where(FIELD_NAME).is(name.toUpperCase());
        Criteria capitalized = where(FIELD_NAME).is(StringUtils.capitalize(name));

        Criteria criteria = new Criteria().orOperator(lowercase, uppercase, capitalized);
        Optional<MongoPlanet> planet = Optional.ofNullable(mongoTemplate.findOne(query(criteria), MongoPlanet.class));

        if (planet.isEmpty()) {
            log.warn("Busca de planeta no banco pelo nome concluída com sucesso. name: {}. Planeta não encontrado.", name);
        }

        log.info("Busca de planeta no banco pelo nome concluída com sucesso. name: {}.", name);
        return planet;
    }

    @Override
    public MongoPlanet save(Planet planet) {
        log.info("Iniciando criação planeta no banco. name: {}.", planet.name());
        MongoPlanet mongoPlanet = MongoPlanet.fromDomain(planet);
        MongoPlanet savedMongoPlanet = mongoTemplate.save(mongoPlanet);

        log.info("Criação planeta no banco concluída com sucesso. id: {}. name: {}.", planet.id(), planet.name());
        return savedMongoPlanet;
    }

    public void deleteById(String id) {
        log.info("Iniciando exclusão de planeta no banco pelo id. id: {}.", id);
        Criteria criteria = where(FIELD_ID).is(id);
        DeleteResult deleteResult = mongoTemplate.remove(criteria);
        if (deleteResult.getDeletedCount() == 0) {
            log.warn("Por razões desconhecidas nenhum planeta foi deletado por id. id: {}.", id);
            //TODO throw not found exception
            return;
        }
        log.info("Exclusão de planeta no banco pelo id concluída com sucesso. id: {}.", id);
    }

}
