package com.api.starwars.planet.repositories;

import com.api.starwars.planet.model.domain.Planet;
import com.api.starwars.planet.model.mongo.MongoPlanet;
import com.mongodb.client.result.DeleteResult;
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

@Repository
public class PlanetRepository implements IPlanetRepository {

    @Qualifier("mongoTemplate")
    private final MongoTemplate mongoTemplate;

    @Autowired
    public PlanetRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Long count() {
        Criteria criteria = where(FIELD_ID).exists(true);
        return mongoTemplate.count(query(criteria), MongoPlanet.class);
    }

    @Override
    public Optional<MongoPlanet> findbyId(String id) {
        Criteria criteria = where(FIELD_ID).is(id);
        return Optional.ofNullable(mongoTemplate.findOne(query(criteria), MongoPlanet.class));
    }

    @Override
    public Optional<MongoPlanet> findByName(String name) {
        Criteria lowercase = where(FIELD_NAME).is(name.toLowerCase());
        Criteria uppercase = where(FIELD_NAME).is(name.toUpperCase());
        Criteria capitalized = where(FIELD_NAME).is(StringUtils.capitalize(name));

        Criteria criteria = new Criteria().orOperator(lowercase, uppercase, capitalized);

        return Optional.ofNullable(mongoTemplate.findOne(query(criteria), MongoPlanet.class));
    }

    @Override
    public MongoPlanet save(Planet planet) {
        MongoPlanet mongoPlanet = MongoPlanet.fromDomain(planet);
        return mongoTemplate.save(mongoPlanet);
    }

    public void deleteById(String id) {
        Criteria criteria = where(FIELD_ID).is(id);
        DeleteResult deleteResult = mongoTemplate.remove(criteria);
//        if(deleteResult.getDeletedCount() == 0) {
//            //TODO implementar log
//            //TODO throw not found exception
//        }
    }

}
