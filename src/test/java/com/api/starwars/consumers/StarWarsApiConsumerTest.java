package com.api.starwars.consumers;

import com.api.starwars.consumers.dtos.PlanetResponseDTO;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class StarWarsApiConsumerTest {

    @Autowired
    StarWarsApiConsumer starWarsApiConsumer;

    private static final String RIGHT_NAME = "tatooine";
    private static final String WRONG_NAME = "tatooine_do_pedaco";
    private static final Integer OK_STAUTS = 200;

    @Test
    public void should_return_a_not_empty_json_when_find_by_right_name() throws IOException, InterruptedException {
        PlanetResponseDTO planet = starWarsApiConsumer.getPlanetBy(RIGHT_NAME);
        assertNotNull(planet);
        assertEquals(planet.getStatusCode(),OK_STAUTS);
        assertNotNull(planet.getPlanetBodyDto());
        assertTrue(planet.getPlanetBodyDto().getCount() > 0);
    }

    @Test
    public void should_return_an_empty_json_when_find_by_wrong_name() throws IOException, InterruptedException {
        PlanetResponseDTO planet = starWarsApiConsumer.getPlanetBy(WRONG_NAME);
        assertNotNull(planet);
        assertEquals(planet.getStatusCode(),OK_STAUTS);
        assertNotNull(planet.getPlanetBodyDto());
        assertEquals((int) planet.getPlanetBodyDto().getCount(), 0);
        assertTrue(planet.getPlanetBodyDto().getResults().isEmpty());
    }

}
