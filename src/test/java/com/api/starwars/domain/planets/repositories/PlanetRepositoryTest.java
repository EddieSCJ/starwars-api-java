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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class PlanetRepositoryTest {

    private final String FAKE_ID = "fake_id";
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
    public void findByIdSuccessful() {
        Criteria criteria = where(FIELD_ID).is(FAKE_ID);

        planetRepository.findbyId(FAKE_ID);
        verify(mongoTemplate, times(1)).findOne(eq(query(criteria)), eq(MongoPlanet.class));
    }

    @Test
    public void findByIdFailWheNotFound() {
        Criteria criteria = where(FIELD_ID).is(FAKE_ID);
        when(mongoTemplate.findOne(query(criteria), eq(any()))).thenReturn(Optional.empty());

        try {
            planetRepository.findbyId(FAKE_ID);
            verify(mongoTemplate, times(1)).findOne(eq(query(criteria)), eq(MongoPlanet.class));
        }
        catch (HttpNotFoundException httpNotFoundException) {
            assertTrue(StringUtils.hasText(httpNotFoundException.getMessage()));
        }

    }

    @Test
    public void findByNameSuccessful() {
        final String FAKE_NAME = "fake_name";
        Criteria criteria = findByNameCriteria(FAKE_NAME);

        planetRepository.findByName(FAKE_NAME);
        verify(mongoTemplate, times(1)).findOne(eq(query(criteria)), eq(MongoPlanet.class));
    }

    @Test
    public void findByNameFailWhenNotFound() {
        final String FAKE_NAME = "fake_name";
        Criteria criteria = findByNameCriteria(FAKE_NAME);

        when(mongoTemplate.findOne(query(criteria), eq(any()))).thenReturn(Optional.empty());

        planetRepository.findByName(FAKE_NAME);
        verify(mongoTemplate, times(1)).findOne(eq(query(criteria)), eq(MongoPlanet.class));
    }

    @Test
    public void saveSuccessful() {
        LocalDateTime dateTime = LocalDateTime.now();

        try (MockedStatic<LocalDateTime> mock = Mockito.mockStatic(LocalDateTime.class)) {
            mock.when(LocalDateTime::now).thenReturn(dateTime);

            Planet planet = Domain.getRandomPlanet();
            planetRepository.save(planet);

            verify(mongoTemplate, times(1)).save(eq(MongoPlanet.fromDomain(planet)));
        }

    }

    @Test
    public void deleteByIdSuccessful() {
        Criteria criteria = where(FIELD_ID).is(FAKE_ID);

        when(mongoTemplate.remove(criteria)).thenReturn(DeleteResult.acknowledged(1L));

        planetRepository.deleteById(FAKE_ID);
        verify(mongoTemplate, times(1)).remove(criteria);
    }

    @Test()
    public void deleteByIdFailWhenNotFound() {
        Criteria criteria = where(FIELD_ID).is(FAKE_ID);
        when(mongoTemplate.remove(criteria)).thenReturn(DeleteResult.acknowledged(0L));

        try {
            planetRepository.deleteById(FAKE_ID);
        } catch (HttpNotFoundException httpNotFoundException) {
            assertTrue(StringUtils.hasText(httpNotFoundException.getMessage()));
        }
    }

    public Criteria findByNameCriteria(String FAKE_NAME) {
        Criteria lowercase = where(FIELD_NAME).is(FAKE_NAME.toLowerCase());
        Criteria uppercase = where(FIELD_NAME).is(FAKE_NAME.toUpperCase());
        Criteria capitalized = where(FIELD_NAME).is(StringUtils.capitalize(FAKE_NAME));

        return new Criteria().orOperator(lowercase, uppercase, capitalized);
    }

}
