package com.api.starwars.domain.planets.repositories;

import com.api.starwars.commons.exceptions.http.HttpNotFoundException;
import com.api.starwars.domain.planets.model.domain.Planet;
import com.api.starwars.domain.planets.model.mongo.MongoPlanet;
import com.mongodb.client.result.DeleteResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.StringUtils;
import utils.Domain;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.api.starwars.domain.planets.model.mongo.Constants.FIELD_ID;
import static com.api.starwars.domain.planets.model.mongo.Constants.FIELD_NAME;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static utils.Domain.FAKE_ID;

public class PlanetRepositoryTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private PlanetRepository planetRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void count() {
        Criteria criteria = where(FIELD_ID).exists(true);

        planetRepository.count();
        verify(mongoTemplate, times(1)).count(eq(query(criteria)), eq(MongoPlanet.class));
    }

    @Test
    public void find_by_id_successful() {
        Criteria criteria = where(FIELD_ID).is(FAKE_ID);
        when(mongoTemplate.findOne(eq(query(criteria)), eq(MongoPlanet.class))).thenReturn(Domain.getRandomMongoPlanet());

        planetRepository.findById(FAKE_ID);
        verify(mongoTemplate, times(1)).findOne(eq(query(criteria)), eq(MongoPlanet.class));
    }

    @Test
    public void find_by_id_fail_when_not_found() {
        Criteria criteria = where(FIELD_ID).is(FAKE_ID);
        when(mongoTemplate.findOne(query(criteria), eq(any()))).thenReturn(Optional.empty());

        assertThrows(HttpNotFoundException.class, () -> planetRepository.findById(FAKE_ID));
        verify(mongoTemplate, times(1)).findOne(eq(query(criteria)), eq(MongoPlanet.class));

    }

    @Test
    public void find_by_name_successful() {
        final String FAKE_NAME = "fake_name";
        Criteria criteria = getFindByNameCriteria(FAKE_NAME);

        planetRepository.findByName(FAKE_NAME);
        verify(mongoTemplate, times(1)).findOne(eq(query(criteria)), eq(MongoPlanet.class));
    }

    @Test
    public void find_by_name_fail_when_not_found() {
        final String FAKE_NAME = "fake_name";
        Criteria criteria = getFindByNameCriteria(FAKE_NAME);

        when(mongoTemplate.findOne(query(criteria), eq(any()))).thenReturn(Optional.empty());

        planetRepository.findByName(FAKE_NAME);
        verify(mongoTemplate, times(1)).findOne(eq(query(criteria)), eq(MongoPlanet.class));
    }

    @Test
    public void save_successful() {
        LocalDateTime dateTime = LocalDateTime.now();

        try (MockedStatic<LocalDateTime> mock = Mockito.mockStatic(LocalDateTime.class)) {
            mock.when(LocalDateTime::now).thenReturn(dateTime);

            Planet planet = Domain.getRandomPlanet();
            planetRepository.save(planet);

            verify(mongoTemplate, times(1)).save(eq(MongoPlanet.fromDomain(planet)));
        }

    }

    @Test
    public void delete_by_id_successful() {
        Criteria criteria = where(FIELD_ID).is(FAKE_ID);

        when(mongoTemplate.remove(criteria)).thenReturn(DeleteResult.acknowledged(1L));

        planetRepository.deleteById(FAKE_ID);
        verify(mongoTemplate, times(1)).remove(criteria);
    }

    @Test()
    public void delete_by_id_fail_when_not_found() {
        Criteria criteria = where(FIELD_ID).is(FAKE_ID);
        when(mongoTemplate.remove(criteria)).thenReturn(DeleteResult.acknowledged(0L));

        assertThrows(HttpNotFoundException.class, () -> planetRepository.deleteById(FAKE_ID));
    }

    public Criteria getFindByNameCriteria(String FAKE_NAME) {
        Criteria lowercase = where(FIELD_NAME).is(FAKE_NAME.toLowerCase());
        Criteria uppercase = where(FIELD_NAME).is(FAKE_NAME.toUpperCase());
        Criteria capitalized = where(FIELD_NAME).is(StringUtils.capitalize(FAKE_NAME));

        return new Criteria().orOperator(lowercase, uppercase, capitalized);
    }

}
