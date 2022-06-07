package commons.utils;

import com.api.starwars.planets.app.storage.mongo.model.MongoPlanet;
import com.api.starwars.planets.domain.model.Planet;
import com.api.starwars.planets.domain.model.client.PlanetJson;
import com.api.starwars.planets.domain.model.client.PlanetResponseJson;
import com.api.starwars.planets.domain.model.view.PlanetView;
import com.github.javafaker.Faker;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DomainUtils {

    public static final String FAKE_ID = ObjectId.get().toHexString();

    private static final Faker FAKER = new Faker();
    private static final Random RANDOM = new Random();

    public static Planet getRandomPlanet() {
        String[] weathers = new String[]{FAKER.weather().description(), FAKER.weather().description()};
        String[] terrain = new String[]{FAKER.country().name(), FAKER.country().name()};
        return new Planet(
                ObjectId.get().toString(),
                FAKER.pokemon().name(),
                weathers,
                terrain,
                RANDOM.nextInt(),
                0L
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

        PlanetJson planetJson = new PlanetJson(
                FAKER.pokemon().name(),
                weathers,
                terrain,
                films
        );

        PlanetResponseJson planetResponseJson = new PlanetResponseJson();
        List<PlanetJson> planets = List.of(planetJson);

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

    public static PlanetView getRandomPlanetJson() {
        String[] weathers = new String[]{FAKER.weather().description(), FAKER.weather().description()};
        String[] terrain = new String[]{FAKER.country().name(), FAKER.country().name()};

        return new PlanetView(
          FAKER.idNumber().toString(),
          FAKER.pokemon().name(),
          weathers,
          terrain,
          RANDOM.nextLong(),
          RANDOM.nextInt()
        );
    }

    public static PlanetView getInvalidPlanetJson() {
        return new PlanetView(
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

}
