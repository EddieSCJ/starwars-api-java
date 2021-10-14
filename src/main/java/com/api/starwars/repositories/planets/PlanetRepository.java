package com.api.starwars.repositories.planets;

import com.api.starwars.model.mongo.MongoPlanet;
import com.api.starwars.repositories.IPlanetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.api.starwars.model.mongo.MongoPlanet.FIELD_ID;
import static com.api.starwars.model.mongo.MongoPlanet.FIELD_NAME;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
public class PlanetRepository implements IPlanetRepository {


    @Autowired
    @Qualifier("mongoTemplate")
    private MongoTemplate mongoTemplate;

    public List<MongoPlanet> findAll() {
        return mongoTemplate.findAll(MongoPlanet.class);

    }

    @Override
    public MongoPlanet findByName(String name) {
        Criteria criteria = where(FIELD_NAME).is(name);
        return mongoTemplate.findOne(query(criteria), MongoPlanet.class);
    }

    @Override
    public MongoPlanet findbyId(String id) {
        Criteria criteria = where(FIELD_ID).is(id);
        return mongoTemplate.findOne(query(criteria), MongoPlanet.class);
    }

    @Override
    public MongoPlanet findByNameOrId(String name, String id) {
        Criteria findById = where(FIELD_ID).is(id);
        Criteria criteria = where(FIELD_NAME).is(name)
                .orOperator(findById);

        return mongoTemplate.findOne(query(criteria), MongoPlanet.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public MongoPlanet save(MongoPlanet mongoPlanet) {
        return mongoTemplate.save(mongoPlanet);
    }

}
