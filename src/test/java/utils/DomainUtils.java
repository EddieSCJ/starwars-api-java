package utils;

import com.api.starwars.domain.planets.clients.view.MPlanetJson;
import com.api.starwars.domain.planets.clients.view.PlanetResponseJson;
import com.api.starwars.domain.planets.model.domain.Planet;
import com.api.starwars.domain.planets.model.mongo.MongoPlanet;
import com.api.starwars.domain.planets.model.view.PlanetJson;
import com.github.javafaker.Faker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DomainUtils {

    public static final String FAKE_ID = "fake_id";

    private static final Faker FAKER = new Faker();
    private static final Random RANDOM = new Random();

    public static Planet getRandomPlanet() {
        String[] weathers = new String[]{FAKER.weather().description(), FAKER.weather().description()};
        String[] terrain = new String[]{FAKER.country().name(), FAKER.country().name()};
        return new Planet(
                FAKER.idNumber().toString(),
                FAKER.pokemon().name(),
                weathers,
                terrain,
                RANDOM.nextInt(),
                RANDOM.nextLong()
        );
    }

    public static List<Planet> getRandomPlanetList() {
        List<Planet> planets = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            planets.add(getRandomPlanet());
        }

        return planets;
    }


    public static Planet getInvalidPlanet() {
        return new Planet(
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public static MongoPlanet getRandomMongoPlanet() {
        return MongoPlanet.fromDomain(getRandomPlanet());
    }

    public static List<MongoPlanet> getRandomMongoPlanetList() {
        List<MongoPlanet> mongoPlanets = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            mongoPlanets.add(getRandomMongoPlanet());
        }

        return mongoPlanets;
    }

    public static PlanetResponseJson getRandomPlanetResponseJson() {
        List<String> films = List.of(FAKER.rickAndMorty().location(), FAKER.rickAndMorty().location());
        String weathers = FAKER.weather().description()
                .concat(",")
                .concat(FAKER.weather().description());

        String terrain = FAKER.country().name()
                .concat(",")
                .concat(FAKER.country().name());

        MPlanetJson mPlanetJson = new MPlanetJson(
                FAKER.pokemon().name(),
                weathers,
                terrain,
                films
        );

        PlanetResponseJson planetResponseJson = new PlanetResponseJson();
        List<MPlanetJson> planets = List.of(mPlanetJson);

        planetResponseJson.setResults(planets);
        planetResponseJson.setCount(1);

        return planetResponseJson;
    }

    public static PlanetResponseJson getEmptyPlanetResponseJson() {
        PlanetResponseJson planetResponseJson = getRandomPlanetResponseJson();
        planetResponseJson.setResults(new ArrayList<>());
        planetResponseJson.setCount(0);

        return planetResponseJson;
    }

    public static PlanetJson getRandomPlanetJson() {
        String[] weathers = new String[]{FAKER.weather().description(), FAKER.weather().description()};
        String[] terrain = new String[]{FAKER.country().name(), FAKER.country().name()};

        return new PlanetJson(
          FAKER.idNumber().toString(),
          FAKER.pokemon().name(),
          weathers,
          terrain,
          RANDOM.nextLong(),
          RANDOM.nextInt()
        );
    }
}
